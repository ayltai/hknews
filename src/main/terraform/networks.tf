resource "aws_api_gateway_rest_api" "this" {
  name = var.tag

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

resource "aws_api_gateway_deployment" "this" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  stage_name  = var.api_stage_name

  triggers = {
    redeployment = sha1(join(",", list(jsonencode(module.lambda_source), jsonencode(module.lambda_item), jsonencode(module.lambda_items))))
  }

  lifecycle {
    create_before_destroy = true
  }

  depends_on = [
    module.lambda_source,
    module.lambda_item,
    module.lambda_items,
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
