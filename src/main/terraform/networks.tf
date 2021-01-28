resource "aws_api_gateway_rest_api" "this" {
  name = var.tag
}

resource "aws_api_gateway_method_settings" "this" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  method_path = "*/*"
  stage_name  = var.api_stage_name

  settings {
    logging_level          = "ERROR"
    metrics_enabled        = true
    throttling_rate_limit  = var.api_throttle_limit
    throttling_burst_limit = var.api_throttle_limit
  }
}

resource "aws_api_gateway_resource" "source" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_rest_api.this.root_resource_id
  path_part   = "sources"
}

resource "aws_api_gateway_method" "source" {
  rest_api_id   = aws_api_gateway_rest_api.this.id
  resource_id   = aws_api_gateway_resource.source.id
  http_method   = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_method_response" "source" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  resource_id = aws_api_gateway_resource.source.id
  http_method = aws_api_gateway_method.source.http_method
  status_code = "200"

  response_models = {
    "application/json" = "Empty"
  }

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = true
    "method.response.header.Access-Control-Allow-Methods" = true
    "method.response.header.Access-Control-Allow-Origin"  = true
  }

  depends_on = [
    aws_api_gateway_integration.source,
  ]
}

resource "aws_api_gateway_integration" "source" {
  rest_api_id             = aws_api_gateway_rest_api.this.id
  resource_id             = aws_api_gateway_resource.source.id
  http_method             = aws_api_gateway_method.source.http_method
  integration_http_method = "POST"
  type                    = "AWS"
  uri                     = aws_lambda_function.source.invoke_arn
}

resource "aws_api_gateway_integration_response" "source" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  resource_id = aws_api_gateway_resource.source.id
  http_method = aws_api_gateway_method.source.http_method
  status_code = aws_api_gateway_method_response.source.status_code

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'"
    "method.response.header.Access-Control-Allow-Methods" = "'GET,HEAD,OPTIONS'"
    "method.response.header.Access-Control-Allow-Origin"  = "'*'"
  }
}

resource "aws_api_gateway_resource" "item" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_rest_api.this.root_resource_id
  path_part   = "item"
}

resource "aws_api_gateway_resource" "item_uid" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.item.id
  path_part   = "{uid}"
}

resource "aws_api_gateway_method" "item_uid" {
  rest_api_id   = aws_api_gateway_rest_api.this.id
  resource_id   = aws_api_gateway_resource.item_uid.id
  http_method   = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_method_response" "item_uid" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  resource_id = aws_api_gateway_resource.item_uid.id
  http_method = aws_api_gateway_method.item_uid.http_method
  status_code = "200"

  response_models = {
    "application/json" = "Empty"
  }

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = true
    "method.response.header.Access-Control-Allow-Methods" = true
    "method.response.header.Access-Control-Allow-Origin"  = true
  }

  depends_on = [
    aws_api_gateway_integration.item_uid,
  ]
}

resource "aws_api_gateway_integration" "item_uid" {
  rest_api_id             = aws_api_gateway_rest_api.this.id
  resource_id             = aws_api_gateway_resource.item_uid.id
  http_method             = aws_api_gateway_method.item_uid.http_method
  integration_http_method = "POST"
  type                    = "AWS"
  uri                     = aws_lambda_function.item.invoke_arn
}

resource "aws_api_gateway_integration_response" "item_uid" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  resource_id = aws_api_gateway_resource.item_uid.id
  http_method = aws_api_gateway_method.item_uid.http_method
  status_code = aws_api_gateway_method_response.item_uid.status_code

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'"
    "method.response.header.Access-Control-Allow-Methods" = "'GET,HEAD,OPTIONS'"
    "method.response.header.Access-Control-Allow-Origin"  = "'*'"
  }
}

resource "aws_api_gateway_resource" "items" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_rest_api.this.root_resource_id
  path_part   = "items"
}

resource "aws_api_gateway_resource" "items_source" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.items.id
  path_part   = "{sourceNames}"
}

resource "aws_api_gateway_resource" "items_source_category" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.items_source.id
  path_part   = "{categoryNames}"
}

resource "aws_api_gateway_resource" "items_source_category_day" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.items_source_category.id
  path_part   = "{days}"
}

resource "aws_api_gateway_method" "items_source_category_day" {
  rest_api_id   = aws_api_gateway_rest_api.this.id
  resource_id   = aws_api_gateway_resource.items_source_category_day.id
  http_method   = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_method_response" "items_source_category_day" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  resource_id = aws_api_gateway_resource.items_source_category_day.id
  http_method = aws_api_gateway_method.items_source_category_day.http_method
  status_code = "200"

  response_models = {
    "application/json" = "Empty"
  }

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = true
    "method.response.header.Access-Control-Allow-Methods" = true
    "method.response.header.Access-Control-Allow-Origin"  = true
  }

  depends_on = [
    aws_api_gateway_integration.items_source_category_day,
  ]
}

resource "aws_api_gateway_integration" "items_source_category_day" {
  rest_api_id             = aws_api_gateway_rest_api.this.id
  resource_id             = aws_api_gateway_resource.items_source_category_day.id
  http_method             = aws_api_gateway_method.items_source_category_day.http_method
  integration_http_method = "POST"
  type                    = "AWS"
  uri                     = aws_lambda_function.items.invoke_arn
}

resource "aws_api_gateway_integration_response" "items_source_category_day" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  resource_id = aws_api_gateway_resource.items_source_category_day.id
  http_method = aws_api_gateway_method.items_source_category_day.http_method
  status_code = aws_api_gateway_method_response.items_source_category_day.status_code

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'"
    "method.response.header.Access-Control-Allow-Methods" = "'GET,HEAD,OPTIONS'"
    "method.response.header.Access-Control-Allow-Origin"  = "'*'"
  }
}

resource "aws_api_gateway_deployment" "this" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  stage_name  = var.api_stage_name

  depends_on = [
    aws_api_gateway_integration.source,
    aws_api_gateway_integration_response.source,
    aws_api_gateway_integration.item_uid,
    aws_api_gateway_integration_response.item_uid,
    aws_api_gateway_integration.items_source_category_day,
    aws_api_gateway_integration_response.items_source_category_day,
  ]
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

resource "aws_cloudfront_distribution" "this" {
  enabled             = true
  default_root_object = "index.html"
  price_class         = var.aws_cloudfront_price_class

  aliases = [
    var.app_domain,
    "www.${var.app_domain}",
  ]

  origin {
    origin_id   = var.app_domain
    domain_name = aws_s3_bucket.this.website_endpoint

    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "http-only"

      origin_ssl_protocols = [
        "TLSv1",
        "TLSv1.1",
        "TLSv1.2",
      ]
    }
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    acm_certificate_arn = aws_acm_certificate_validation.this.certificate_arn
    ssl_support_method  = "sni-only"
  }

  default_cache_behavior {
    target_origin_id       = var.app_domain
    viewer_protocol_policy = "redirect-to-https"
    compress               = true
    min_ttl                = 0
    max_ttl                = 1209600
    default_ttl            = 86400

    allowed_methods = [
      "GET",
      "HEAD",
    ]

    cached_methods = [
      "GET",
      "HEAD",
    ]

    forwarded_values {
      query_string = false

      cookies {
        forward = "none"
      }
    }
  }

  tags = {
    Project = var.tag
  }
}
