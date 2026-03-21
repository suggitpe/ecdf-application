provider "google" {
  project = var.project_id
  region  = var.region
}

# 1. Artifact Registry for Docker Images
resource "google_artifact_registry_repository" "ecdf_repo" {
  location      = var.region
  repository_id = "${var.app_name}-repo"
  description   = "Docker repository for the ECDF application"
  format        = "DOCKER"
}

# 2. Cloud Storage Bucket for file attachments
resource "google_storage_bucket" "ecdf_storage" {
  name          = "${var.project_id}-ecdf-storage"
  location      = var.region
  force_destroy = true

  uniform_bucket_level_access = true
}

# 3. Service Account for the application
resource "google_service_account" "app_sa" {
  account_id   = "${var.app_name}-sa"
  display_name = "Service Account for ECDF Application"
}

# IAM Role for Storage Access
resource "google_storage_bucket_iam_member" "storage_admin" {
  bucket = google_storage_bucket.ecdf_storage.name
  role   = "roles/storage.objectAdmin"
  member = "serviceAccount:${google_service_account.app_sa.email}"
}

# 4. Cloud Run Service (v2)
resource "google_cloud_run_v2_service" "app" {
  name     = var.app_name
  location = var.region

  template {
    service_account = google_service_account.app_sa.email

    containers {
      # This image placeholder assumes you have pushed the image to the registry
      image = "${var.region}-docker.pkg.dev/${var.project_id}/${google_artifact_registry_repository.ecdf_repo.name}/${var.app_name}:latest"

      ports {
        container_port = 8080
      }

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "gcp"
      }

      env {
        name  = "FILE_STORAGE_PATH"
        value = "/data/storage"
      }

      # Mount GCS bucket to /data/storage using GCS FUSE
      volume_mounts {
        name       = "ecdf-vol"
        mount_path = "/data/storage"
      }
    }

    volumes {
      name = "ecdf-vol"
      gcs {
        bucket    = google_storage_bucket.ecdf_storage.name
        read_only = false
      }
    }

    execution_environment = "EXECUTION_ENVIRONMENT_GEN2"
  }
}

# 5. Allow unauthenticated access (publicly reachable)
resource "google_cloud_run_v2_service_iam_member" "public_access" {
  location = google_cloud_run_v2_service.app.location
  name     = google_cloud_run_v2_service.app.name
  role     = "roles/run.invoker"
  member   = "allUsers"
}
