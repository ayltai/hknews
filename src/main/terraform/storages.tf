data "aws_iam_policy_document" "s3" {
  version = "2012-10-17"

  statement {
    effect = "Allow"

    principals {
      type = "AWS"

      identifiers = [
        "*",
      ]
    }

    resources = [
      "arn:aws:s3:::${var.tag}-${var.app_version}/*",
    ]

    actions = [
      "s3:GetObject",
    ]
  }
}

resource "aws_s3_bucket" "this" {
  bucket = "${var.tag}-${var.app_version}"
  acl    = "public-read"
  policy = data.aws_iam_policy_document.s3.json

  website {
    index_document = "index.html"
    error_document = "index.html"
  }

  cors_rule {
    allowed_origins = [
      "*",
    ]

    allowed_methods = [
      "GET",
      "HEAD",
    ]

    allowed_headers = [
      "*",
    ]
  }

  tags = {
    Project = var.tag
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
