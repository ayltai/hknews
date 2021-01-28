output "base_uri" {
  value = aws_api_gateway_deployment.this.invoke_url
}

output "s3_bucket" {
  value = aws_s3_bucket.this.bucket
}
