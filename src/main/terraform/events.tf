resource "aws_cloudwatch_event_rule" "appledaily" {
  name                = "appledaily"
  schedule_expression = "cron(0/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "appledaily" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.appledaily.name
  arn       = aws_lambda_function.appledaily.arn
}

resource "aws_cloudwatch_event_rule" "headline" {
  name                = "headline"
  schedule_expression = "cron(1/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "headline" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.headline.name
  arn       = aws_lambda_function.headline.arn
}

resource "aws_cloudwatch_event_rule" "headline_realtime" {
  name                = "headline_realtime"
  schedule_expression = "cron(2/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "headline_realtime" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.headline_realtime.name
  arn       = aws_lambda_function.headline_realtime.arn
}

resource "aws_cloudwatch_event_rule" "hkej" {
  name                = "hkej"
  schedule_expression = "cron(3/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "hkej" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.hkej.name
  arn       = aws_lambda_function.hkej.arn
}

resource "aws_cloudwatch_event_rule" "hkej_realtime" {
  name                = "hkej_realtime"
  schedule_expression = "cron(4/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "hkej_realtime" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.hkej_realtime.name
  arn       = aws_lambda_function.hkej_realtime.arn
}

resource "aws_cloudwatch_event_rule" "hket" {
  name                = "hket"
  schedule_expression = "cron(5/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "hket" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.hket.name
  arn       = aws_lambda_function.hket.arn
}

resource "aws_cloudwatch_event_rule" "mingpao" {
  name                = "mingpao"
  schedule_expression = "cron(6/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "mingpao" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.mingpao.name
  arn       = aws_lambda_function.mingpao.arn
}

resource "aws_cloudwatch_event_rule" "orientaldaily" {
  name                = "orientaldaily"
  schedule_expression = "cron(7/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "orientaldaily" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.orientaldaily.name
  arn       = aws_lambda_function.orientaldaily.arn
}

resource "aws_cloudwatch_event_rule" "orientaldaily_realtime" {
  name                = "orientaldaily_realtime"
  schedule_expression = "cron(8/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "orientaldaily_realtime" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.orientaldaily_realtime.name
  arn       = aws_lambda_function.orientaldaily_realtime.arn
}

resource "aws_cloudwatch_event_rule" "rthk" {
  name                = "rthk"
  schedule_expression = "cron(9/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "rthk" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.rthk.name
  arn       = aws_lambda_function.rthk.arn
}

resource "aws_cloudwatch_event_rule" "scmp" {
  name                = "scmp"
  schedule_expression = "cron(10/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "scmp" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.scmp.name
  arn       = aws_lambda_function.scmp.arn
}

resource "aws_cloudwatch_event_rule" "singpao" {
  name                = "singpao"
  schedule_expression = "cron(11/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "singpao" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.singpao.name
  arn       = aws_lambda_function.singpao.arn
}

resource "aws_cloudwatch_event_rule" "singtao" {
  name                = "singtao"
  schedule_expression = "cron(12/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "singtao" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.singtao.name
  arn       = aws_lambda_function.singtao.arn
}

resource "aws_cloudwatch_event_rule" "skypost" {
  name                = "skypost"
  schedule_expression = "cron(13/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "skypost" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.skypost.name
  arn       = aws_lambda_function.skypost.arn
}

resource "aws_cloudwatch_event_rule" "thestandard" {
  name                = "thestandard"
  schedule_expression = "cron(14/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "thestandard" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.thestandard.name
  arn       = aws_lambda_function.thestandard.arn
}

resource "aws_cloudwatch_event_rule" "wenweipo" {
  name                = "wenweipo"
  schedule_expression = "cron(15/20 * * * ? *)"

  tags = {
    Project = var.tag
  }
}

resource "aws_cloudwatch_event_target" "wenweipo" {
  target_id = "lambda"
  rule      = aws_cloudwatch_event_rule.wenweipo.name
  arn       = aws_lambda_function.wenweipo.arn
}
