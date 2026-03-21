output "app_url" {
  description = "The URL of the deployed Cloud Run service"
  value       = google_cloud_run_v2_service.app.uri
}

output "artifact_registry_repo" {
  description = "The Artifact Registry repository URL"
  value       = google_artifact_registry_repository.ecdf_repo.repository_id
}

output "docker_push_command" {
  description = "Example command to push your local image to the registry"
  value       = "docker tag ${var.app_name}:latest ${var.region}-docker.pkg.dev/${var.project_id}/${google_artifact_registry_repository.ecdf_repo.name}/${var.app_name}:latest"
}
