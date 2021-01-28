data "aws_iam_policy_document" "lambda" {
  version = "2012-10-17"

  statement {
    sid    = ""
    effect = "Allow"

    principals {
      type = "Service"

      identifiers = [
        "lambda.amazonaws.com",
      ]
    }

    actions = [
      "sts:AssumeRole",
    ]
  }
}

data "aws_iam_policy_document" "dynamodb" {
  version = "2012-10-17"

  statement {
    effect = "Allow"

    resources = [
      "*",
    ]

    actions = [
      "dynamodb:List*",
      "dynamodb:Describe*",
      "dynamodb:CreateTable",
      "dynamodb:Query",
      "dynamodb:Scan",
      "dynamodb:Get*",
      "dynamodb:BatchGet*",
      "dynamodb:Put*",
      "dynamodb:Update*",
      "dynamodb:BatchWrite*",
      "dynamodb:Delete*",
    ]
  }
}

data "aws_iam_policy_document" "cloudwatch" {
  version = "2012-10-17"

  statement {
    effect = "Allow"

    resources = [
      "arn:aws:logs:*:*:*",
    ]

    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents",
    ]
  }
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
