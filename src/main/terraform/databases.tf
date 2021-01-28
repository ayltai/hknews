resource "aws_dynamodb_table" "source" {
  name         = "Source"
  hash_key     = "Url"
  range_key    = "CategoryName"
  billing_mode = "PAY_PER_REQUEST"

  attribute {
    name = "CategoryName"
    type = "S"
  }

  attribute {
    name = "Url"
    type = "S"
  }

  tags = {
    Project = var.tag
  }
}

resource "aws_dynamodb_table" "item" {
  name         = "Item"
  hash_key     = "Uid"
  range_key    = "PublishDate"
  billing_mode = "PAY_PER_REQUEST"

  attribute {
    name = "Uid"
    type = "S"
  }

  attribute {
    name = "PublishDate"
    type = "S"
  }

  tags = {
    Project = var.tag
  }
}
