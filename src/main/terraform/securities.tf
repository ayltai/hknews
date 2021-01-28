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

resource "aws_lambda_permission" "source" {
  statement_id  = "AllowExecutionFromApiGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.source.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.this.execution_arn}/*/*"
}

resource "aws_lambda_permission" "item" {
  statement_id  = "AllowExecutionFromApiGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.item.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.this.execution_arn}/*/*"
}

resource "aws_lambda_permission" "items" {
  statement_id  = "AllowExecutionFromApiGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.items.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.this.execution_arn}/*/*"
}

resource "aws_lambda_permission" "appledaily" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.appledaily.function_name
  source_arn    = aws_cloudwatch_event_rule.appledaily.arn
}

resource "aws_lambda_permission" "headline" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.headline.function_name
  source_arn    = aws_cloudwatch_event_rule.headline.arn
}

resource "aws_lambda_permission" "headline_realtime" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.headline_realtime.function_name
  source_arn    = aws_cloudwatch_event_rule.headline_realtime.arn
}

resource "aws_lambda_permission" "hkej" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.hkej.function_name
  source_arn    = aws_cloudwatch_event_rule.hkej.arn
}

resource "aws_lambda_permission" "hkej_realtime" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.hkej_realtime.function_name
  source_arn    = aws_cloudwatch_event_rule.hkej_realtime.arn
}

resource "aws_lambda_permission" "hket" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.hket.function_name
  source_arn    = aws_cloudwatch_event_rule.hket.arn
}

resource "aws_lambda_permission" "mingpao" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.mingpao.function_name
  source_arn    = aws_cloudwatch_event_rule.mingpao.arn
}

resource "aws_lambda_permission" "orientaldaily" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.orientaldaily.function_name
  source_arn    = aws_cloudwatch_event_rule.orientaldaily.arn
}

resource "aws_lambda_permission" "orientaldaily_realtime" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.orientaldaily_realtime.function_name
  source_arn    = aws_cloudwatch_event_rule.orientaldaily_realtime.arn
}

resource "aws_lambda_permission" "rthk" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.rthk.function_name
  source_arn    = aws_cloudwatch_event_rule.rthk.arn
}

resource "aws_lambda_permission" "scmp" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.scmp.function_name
  source_arn    = aws_cloudwatch_event_rule.scmp.arn
}

resource "aws_lambda_permission" "singpao" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.singpao.function_name
  source_arn    = aws_cloudwatch_event_rule.singpao.arn
}

resource "aws_lambda_permission" "singtao" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.singtao.function_name
  source_arn    = aws_cloudwatch_event_rule.singtao.arn
}

resource "aws_lambda_permission" "skypost" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.skypost.function_name
  source_arn    = aws_cloudwatch_event_rule.skypost.arn
}

resource "aws_lambda_permission" "thestandard" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.thestandard.function_name
  source_arn    = aws_cloudwatch_event_rule.thestandard.arn
}

resource "aws_lambda_permission" "wenweipo" {
  statement_id  = "AllowExecutionFromCloudWatch"
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.wenweipo.function_name
  source_arn    = aws_cloudwatch_event_rule.wenweipo.arn
}
