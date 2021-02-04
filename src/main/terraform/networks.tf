locals {
  status_codes = [
    "200", "400", "404", "500",
  ]
}

data "template_file" "api" {
  template = file("./templates/api.yaml")

  vars = {
    base_uri = aws_api_gateway_deployment.this.invoke_url
  }
}

resource "aws_api_gateway_rest_api" "this" {
  name                     = var.tag
  minimum_compression_size = var.api_minimum_compression_size

  tags = {
    Project = var.tag
  }
}

resource "aws_api_gateway_method_settings" "this" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  method_path = "*/*"
  stage_name  = aws_api_gateway_deployment.this.stage_name

  settings {
    logging_level          = "ERROR"
    metrics_enabled        = true
    throttling_rate_limit  = var.api_throttle_limit
    throttling_burst_limit = var.api_throttle_limit
  }
}

resource "aws_api_gateway_method" "root" {
  rest_api_id   = aws_api_gateway_rest_api.this.id
  resource_id   = aws_api_gateway_rest_api.this.root_resource_id
  http_method   = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_method_response" "root" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  resource_id = aws_api_gateway_rest_api.this.root_resource_id
  http_method = aws_api_gateway_method.root.http_method
  status_code = "200"

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = true
    "method.response.header.Access-Control-Allow-Methods" = true
    "method.response.header.Access-Control-Allow-Origin"  = true
  }
}

resource "aws_api_gateway_integration" "root" {
  rest_api_id          = aws_api_gateway_rest_api.this.id
  resource_id          = aws_api_gateway_rest_api.this.root_resource_id
  http_method          = aws_api_gateway_method.root.http_method
  type                 = "MOCK"
  passthrough_behavior = "WHEN_NO_MATCH"

  request_templates = {
    "application/json" = "{\"statusCode\": 200}"
  }
}

resource "aws_api_gateway_integration_response" "root" {
  rest_api_id        = aws_api_gateway_rest_api.this.id
  resource_id        = aws_api_gateway_rest_api.this.root_resource_id
  http_method        = aws_api_gateway_method.root.http_method
  status_code        = aws_api_gateway_method_response.root.status_code
  response_templates = aws_api_gateway_integration.root.request_templates

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = "'Content-Type'"
    "method.response.header.Access-Control-Allow-Methods" = "'GET,HEAD,OPTIONS'"
    "method.response.header.Access-Control-Allow-Origin"  = "'*'"
  }

  depends_on = [
    aws_api_gateway_integration.root,
  ]
}

resource "aws_api_gateway_deployment" "this" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  stage_name  = var.api_stage_name

  triggers = {
    redeployment = sha1(join(",", list(jsonencode(module.lambda_source), jsonencode(module.lambda_item), jsonencode(module.lambda_items), jsonencode(aws_api_gateway_integration.root))))
  }

  lifecycle {
    create_before_destroy = true
  }

  depends_on = [
    module.lambda_source,
    module.lambda_item,
    module.lambda_items,
    aws_api_gateway_integration.root,
    aws_api_gateway_integration_response.root,
  ]
}

resource "aws_cloudwatch_log_group" "this" {
  name              = "API-Gateway-Execution-Logs_${aws_api_gateway_rest_api.this.id}/${var.api_stage_name}"
  retention_in_days = var.api_log_retention
}

resource "aws_route53_zone" "this" {
  name = var.app_domain

  tags = {
    Project = var.tag
  }
}

resource "aws_route53_record" "certificate_validation" {
  for_each = {
    for domain_validation_option in aws_acm_certificate.this.domain_validation_options : domain_validation_option.domain_name => domain_validation_option
  }

  zone_id         = aws_route53_zone.this.id
  name            = each.value.resource_record_name
  type            = each.value.resource_record_type
  ttl             = 60
  allow_overwrite = true

  records = [
    each.value.resource_record_value,
  ]
}

resource "aws_route53_record" "this" {
  zone_id = aws_route53_zone.this.id
  name    = var.app_domain
  type    = "A"

  alias {
    zone_id                = aws_cloudfront_distribution.this.hosted_zone_id
    name                   = aws_cloudfront_distribution.this.domain_name
    evaluate_target_health = false
  }
}
