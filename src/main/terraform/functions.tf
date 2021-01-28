locals {
  parsers = [
    {
      function_name : "AppleDailyParser"
      handler : "com.github.ayltai.hknews.handler.AppleDailyParserHandler::handleRequest"
      event_name : "appledaily"
      schedule_expression : "cron(0/20 * * * ? *)"
    },
    {
      function_name : "HeadlineParser"
      handler : "com.github.ayltai.hknews.handler.HeadlineParserHandler::handleRequest"
      event_name : "headline"
      schedule_expression : "cron(1/20 * * * ? *)"
    },
    {
      function_name : "HeadlineRealtimeParser"
      handler : "com.github.ayltai.hknews.handler.HeadlineRealtimeParserHandler::handleRequest"
      event_name : "headline"
      schedule_expression : "cron(2/20 * * * ? *)"
    },
    {
      function_name : "HkejParser"
      handler : "com.github.ayltai.hknews.handler.HkejParserHandler::handleRequest"
      event_name : "hkej"
      schedule_expression : "cron(3/20 * * * ? *)"
    },
    {
      function_name : "HkejRealtimeParser"
      handler : "com.github.ayltai.hknews.handler.HkejRealtimeParserHandler::handleRequest"
      event_name : "hkej_realtime"
      schedule_expression : "cron(4/20 * * * ? *)"
    },
    {
      function_name : "HketParser"
      handler : "com.github.ayltai.hknews.handler.HketParserHandler::handleRequest"
      event_name : "hket"
      schedule_expression : "cron(5/20 * * * ? *)"
    },
    {
      function_name : "MingPaoParser"
      handler : "com.github.ayltai.hknews.handler.MingPaoParserHandler::handleRequest"
      event_name : "mingpao"
      schedule_expression : "cron(6/20 * * * ? *)"
    },
    {
      function_name : "OrientalDailyParser"
      handler : "com.github.ayltai.hknews.handler.OrientalDailyParserHandler::handleRequest"
      event_name : "orientaldaily"
      schedule_expression : "cron(7/20 * * * ? *)"
    },
    {
      function_name : "OrientalDailyRealtimeParser"
      handler : "com.github.ayltai.hknews.handler.OrientalDailyRealtimeParserHandler::handleRequest"
      event_name : "orientaldaily_realtime"
      schedule_expression : "cron(8/20 * * * ? *)"
    },
    {
      function_name : "RthkParser"
      handler : "com.github.ayltai.hknews.handler.RthkParserHandler::handleRequest"
      event_name : "rthk"
      schedule_expression : "cron(9/20 * * * ? *)"
    },
    {
      function_name : "ScmpParser"
      handler : "com.github.ayltai.hknews.handler.ScmpParserHandler::handleRequest"
      event_name : "scmp"
      schedule_expression : "cron(10/20 * * * ? *)"
    },
    {
      function_name : "SingPaoParser"
      handler : "com.github.ayltai.hknews.handler.SingPaoParserHandler::handleRequest"
      event_name : "singpao"
      schedule_expression : "cron(11/20 * * * ? *)"
    },
    {
      function_name : "SingTaoParser"
      handler : "com.github.ayltai.hknews.handler.SingTaoParserHandler::handleRequest"
      event_name : "singtao"
      schedule_expression : "cron(12/20 * * * ? *)"
    },
    {
      function_name : "SkyPostParser"
      handler : "com.github.ayltai.hknews.handler.SkyPostParserHandler::handleRequest"
      event_name : "skypost"
      schedule_expression : "cron(13/20 * * * ? *)"
    },
    {
      function_name : "TheStandardParser"
      handler : "com.github.ayltai.hknews.handler.TheStandardParserHandler::handleRequest"
      event_name : "thestandard"
      schedule_expression : "cron(14/20 * * * ? *)"
    },
    {
      function_name : "WenWeiPoParser"
      handler : "com.github.ayltai.hknews.handler.WenWeiPoParserHandler::handleRequest"
      event_name : "wenweipo"
      schedule_expression : "cron(15/20 * * * ? *)"
    },
  ]
}

module "lambda_source" {
  source                 = "./modules/lambda_api"
  function_name          = "Source"
  filename               = var.api_package_file
  handler                = "com.github.ayltai.hknews.handler.SourceHandler::handleRequest"
  aws_iam_role_arn       = aws_iam_role.lambda.arn
  rest_api_id            = aws_api_gateway_rest_api.this.id
  rest_api_root_id       = aws_api_gateway_rest_api.this.root_resource_id
  rest_api_execution_arn = aws_api_gateway_rest_api.this.execution_arn
  path_part              = "sources"
  tag                    = var.tag
}

resource "aws_api_gateway_resource" "item" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_rest_api.this.root_resource_id
  path_part   = "item"
}

module "lambda_item" {
  source                 = "./modules/lambda_api"
  function_name          = "Item"
  filename               = var.api_package_file
  handler                = "com.github.ayltai.hknews.handler.ItemHandler::handleRequest"
  aws_iam_role_arn       = aws_iam_role.lambda.arn
  rest_api_id            = aws_api_gateway_rest_api.this.id
  rest_api_root_id       = aws_api_gateway_resource.item.id
  rest_api_execution_arn = aws_api_gateway_rest_api.this.execution_arn
  path_part              = "{uid}"
  tag                    = var.tag
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

module "lambda_items" {
  source                 = "./modules/lambda_api"
  function_name          = "Items"
  filename               = var.api_package_file
  handler                = "com.github.ayltai.hknews.handler.ItemsHandler::handleRequest"
  aws_iam_role_arn       = aws_iam_role.lambda.arn
  rest_api_id            = aws_api_gateway_rest_api.this.id
  rest_api_root_id       = aws_api_gateway_resource.items_source_category.id
  rest_api_execution_arn = aws_api_gateway_rest_api.this.execution_arn
  path_part              = "{days}"
  tag                    = var.tag
}

module "lambda_scheduled" {
  for_each = {
    for parser in local.parsers : parser.function_name => parser
  }

  source              = "./modules/lambda_scheduled"
  function_name       = each.value.function_name
  filename            = var.api_package_file
  handler             = each.value.handler
  aws_iam_role_arn    = aws_iam_role.lambda.arn
  event_name          = each.value.event_name
  schedule_expression = each.value.schedule_expression
  tag                 = var.tag
}
