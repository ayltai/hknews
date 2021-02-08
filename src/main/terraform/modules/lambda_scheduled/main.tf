resource "aws_lambda_function" "this" {
  function_name    = var.function_name
  filename         = var.filename
  source_code_hash = filebase64sha256(var.filename)
  handler          = var.handler
  runtime          = var.runtime
  memory_size      = var.memory
  timeout          = var.timeout
  role             = var.aws_iam_role_arn

  environment {
    variables = {
      AWS_DYNAMODB_URL  = "https://dynamodb.${var.aws_region}.amazonaws.com"
      AWS_DYNAMODB_INIT = false
      AWS_CSM_ENABLED   = true
    }
  }

  depends_on = [
    aws_cloudwatch_log_group.this,
  ]

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_rule" "this" {
  name                = var.event_name
  schedule_expression = var.schedule_expression

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "this" {
  rule      = aws_cloudwatch_event_rule.this.name
  target_id = aws_cloudwatch_event_rule.this.name
  arn       = aws_lambda_function.this.arn
}

resource "aws_cloudwatch_log_group" "this" {
  name              = "/aws/lambda/${var.function_name}"
  retention_in_days = var.log_retention
}

resource "aws_lambda_permission" "this" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.this.function_name
  source_arn    = aws_cloudwatch_event_rule.this.arn
}
