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
