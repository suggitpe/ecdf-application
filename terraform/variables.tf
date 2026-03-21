variable "project_id" {
  description = "The GCP project ID"
  type        = string
  default     = "ecdf-spring-boot-app-2026"
}

variable "region" {
  description = "The GCP region to deploy to"
  type        = string
  default     = "europe-west2"
}

variable "app_name" {
  description = "The name of the application deployed"
  type        = string
  default     = "ecdf-mockup"
}
