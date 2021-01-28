resource "null_resource" "build_javascript" {
  depends_on = [
    aws_api_gateway_deployment.this,
  ]

  triggers = {
    always = timestamp()
  }

  provisioner "local-exec" {
    command = <<-EOF
      cd ../../..
      export NODE_ENV=production
      export REACT_APP_API_ENDPOINT=${aws_api_gateway_deployment.this.invoke_url}
      npm run build
    EOF
  }
}
