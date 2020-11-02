resource "aws_internet_gateway" "this" {
  count  = var.aws_enabled ? 1 : 0
  vpc_id = aws_vpc.this.0.id

  tags = {
    Name = var.tag
  }
}
