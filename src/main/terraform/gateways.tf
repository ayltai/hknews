resource "aws_internet_gateway" "this" {
  count  = var.aws_enabled ? 1 : 0
  vpc_id = aws_vpc.this.id

  tags = {
    Name = var.tag
  }
}
