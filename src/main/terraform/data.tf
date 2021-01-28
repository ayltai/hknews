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
