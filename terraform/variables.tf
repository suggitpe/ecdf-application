variable "project_id" {
  description = "The GCP project ID"
  type        = String
}

variable "region" {
  description = "The GCP region to deploy to"
  type        = string
  default     = "europe-west2"
}

variable "app_name" {
  description = "The name of the application"
  type        = String
  default     = "ecdf-app"
}
