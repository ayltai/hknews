resource "aws_lambda_function" "source" {
  function_name    = "Source"
  description      = "Get a list of supported news publisher names"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.SourceHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 256
  timeout          = 30
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "item" {
  function_name    = "Item"
  description      = "Get a specific news record by its unique ID"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.ItemHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 256
  timeout          = 30
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "items" {
  function_name    = "Items"
  description      = "Get a list of news records of specific news categories and from specific news publishers"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.ItemsHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 256
  timeout          = 30
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "appledaily" {
  function_name    = "AppleDailyParser"
  description      = "Parse news records from Apple Daily News website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.AppleDailyParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "headline" {
  function_name    = "HeadlineParser"
  description      = "Parse news records from Headline News website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.HeadlineParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "headline_realtime" {
  function_name    = "HeadlineRealtimeParser"
  description      = "Parse news records from Headline (realtime) News website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.HeadlineRealtimeParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "hkej" {
  function_name    = "HkejParser"
  description      = "Parse news records from HKEJ News website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.HkejParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "hkej_realtime" {
  function_name    = "HkejRealtimeParser"
  description      = "Parse news records from HKEJ (realtime) News website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.HkejRealtimeParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "hket" {
  function_name    = "HketParser"
  description      = "Parse news records from HKET News website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.HketParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "mingpao" {
  function_name    = "MingPaoParser"
  description      = "Parse news records from Ming Pao website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.MingPaoParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "orientaldaily" {
  function_name    = "OrientalDailyParser"
  description      = "Parse news records from Oriental Daily News website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.OrientalDailyParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "orientaldaily_realtime" {
  function_name    = "OrientalDailyRealtimeParser"
  description      = "Parse news records from Oriental Daily (realtime) News website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.OrientalDailyRealtimeParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "rthk" {
  function_name    = "RthkParser"
  description      = "Parse news records from RTHK News website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.RthkParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "scmp" {
  function_name    = "ScmpParser"
  description      = "Parse news records from SCMP News website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.ScmpParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "singpao" {
  function_name    = "SingPaoParser"
  description      = "Parse news records from Sing Pao website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.SingPaoParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "singtao" {
  function_name    = "SingTaoParser"
  description      = "Parse news records from Sing Tao News website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.SingTaoParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "skypost" {
  function_name    = "SkyPostParser"
  description      = "Parse news records from Sky Post News website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.SkyPostParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "thestandard" {
  function_name    = "TheStandardParser"
  description      = "Parse news records from The Standard News website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.TheStandardParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_lambda_function" "wenweipo" {
  function_name    = "WenWeiPoParser"
  description      = "Parse news records from Wen Wei Po website"
  filename         = var.api_package_file
  source_code_hash = filebase64sha256(var.api_package_file)
  handler          = "com.github.ayltai.hknews.handler.WenWeiPoParserHandler::handleRequest"
  runtime          = var.aws_lambda_runtime
  memory_size      = 512
  timeout          = 600
  role             = aws_iam_role.lambda.arn

  environment {
    variables = {
      AWS_DYNAMODB_URL = "https://dynamodb.${var.aws_region}.amazonaws.com"
    }
  }

  tags = {
    Project = var.tag
  }
}
