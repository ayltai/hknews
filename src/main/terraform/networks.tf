resource "aws_vpc" "this" {
  cidr_block = var.vpc_cidr_block

  tags = {
    Name = var.tag
  }
}

resource "aws_eip" "this" {
  instance = aws_instance.this.id
  vpc      = true

  depends_on = [
    aws_internet_gateway.this,
  ]

  tags = {
    Name = var.tag
  }
}

resource "aws_subnet" "this" {
  vpc_id            = aws_vpc.this.id
  availability_zone = var.zone
  cidr_block        = var.subnet_cidr_block

  tags = {
    Name = var.tag
  }
}

resource "aws_route_table" "this" {
  vpc_id = aws_vpc.this.id

  route {
    gateway_id = aws_internet_gateway.this.id
    cidr_block = "0.0.0.0/0"
  }

  tags = {
    Name = var.tag
  }
}

resource "aws_route_table_association" "this" {
  route_table_id = aws_route_table.this.id
  subnet_id      = aws_subnet.this.id
}
