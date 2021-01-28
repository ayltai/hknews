resource "aws_acm_certificate" "this" {
  provider          = aws.acm_certificate_provider
  domain_name       = "*.${var.app_domain}"
  validation_method = "DNS"

  subject_alternative_names = [
    var.app_domain,
  ]

  lifecycle {
    create_before_destroy = true
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_acm_certificate_validation" "this" {
  provider        = aws.acm_certificate_provider
  certificate_arn = aws_acm_certificate.this.arn

  validation_record_fqdns = [
    for record in aws_route53_record.certificate_validation : record.fqdn
  ]
}

resource "aws_iam_role" "lambda" {
  name               = var.tag
  assume_role_policy = data.aws_iam_policy_document.lambda.json
}

resource "aws_iam_policy" "dynamodb" {
  name   = "dynamodb"
  path   = "/"
  policy = data.aws_iam_policy_document.dynamodb.json
}

resource "aws_iam_role_policy_attachment" "dynamodb" {
  role       = aws_iam_role.lambda.name
  policy_arn = aws_iam_policy.dynamodb.arn
}

resource "aws_iam_policy" "cloudwatch" {
  name   = "cloudwatch"
  path   = "/"
  policy = data.aws_iam_policy_document.cloudwatch.json
}

resource "aws_iam_role_policy_attachment" "cloudwatch" {
  role       = aws_iam_role.lambda.name
  policy_arn = aws_iam_policy.cloudwatch.arn
}
