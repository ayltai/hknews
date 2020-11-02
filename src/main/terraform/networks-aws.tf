resource "aws_vpc" "this" {
  count      = var.aws_enabled ? 1 : 0
  cidr_block = var.vpc_cidr_block

  tags = {
    Name = var.tag
  }
}

resource "aws_eip" "this" {
  count    = var.aws_enabled ? 1 : 0
  instance = aws_instance.this.0.id
  vpc      = true

  depends_on = [
    aws_internet_gateway.this,
  ]

  tags = {
    Name = var.tag
  }
}

resource "aws_subnet" "this" {
  count             = var.aws_enabled ? 1 : 0
  vpc_id            = aws_vpc.this.0.id
  availability_zone = var.aws_zone
  cidr_block        = var.subnet_cidr_block

  tags = {
    Name = var.tag
  }
}

resource "aws_route_table" "this" {
  count  = var.aws_enabled ? 1 : 0
  vpc_id = aws_vpc.this.0.id

  route {
    gateway_id = aws_internet_gateway.this.0.id
    cidr_block = "0.0.0.0/0"
  }

  tags = {
    Name = var.tag
  }
}

resource "aws_route_table_association" "this" {
  count          = var.aws_enabled ? 1 : 0
  route_table_id = aws_route_table.this.0.id
  subnet_id      = aws_subnet.this.0.id
}
