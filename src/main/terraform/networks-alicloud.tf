resource "alicloud_vpc" "this" {
  count      = var.alicloud_enabled ? 1 : 0
  cidr_block = var.vpc_cidr_block

  tags = {
    Name = var.tag
  }
}

resource "alicloud_eip" "this" {
  count = var.alicloud_enabled ? 1 : 0
}

resource "alicloud_eip_association" "this" {
  count         = var.alicloud_enabled ? 1 : 0
  allocation_id = alicloud_eip.this.0.id
  instance_id   = alicloud_instance.this.0.id
}

resource "alicloud_vswitch" "this" {
  count             = var.alicloud_enabled ? 1 : 0
  vpc_id            = alicloud_vpc.this.0.id
  cidr_block        = var.subnet_cidr_block
  availability_zone = var.alicloud_zone
}

resource "alicloud_route_table" "this" {
  count  = var.alicloud_enabled ? 1 : 0
  vpc_id = alicloud_vpc.this.0.id
}

resource "alicloud_route_table_attachment" "this" {
  count          = var.alicloud_enabled ? 1 : 0
  route_table_id = alicloud_route_table.this.0.id
  vswitch_id     = alicloud_vswitch.this.0.id
}
