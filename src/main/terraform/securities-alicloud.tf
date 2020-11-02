resource "alicloud_key_pair" "this" {
  count      = var.alicloud_enabled ? 1 : 0
  key_name   = var.tag
  public_key = tls_private_key.this.public_key_openssh

  tags = {
    Name = var.tag
  }
}

resource "alicloud_security_group" "this" {
  count       = var.alicloud_enabled ? 1 : 0
  name        = var.tag
  description = "HK News security group"
  vpc_id      = alicloud_vpc.this.id

  tags = {
    Name = var.tag
  }
}

resource "alicloud_security_group_rule" "icmp" {
  count             = var.alicloud_enabled ? 1 : 0
  security_group_id = alicloud_security_group.this.id
  ip_protocol       = "icmp"
  type              = "ingress"
}

resource "alicloud_security_group_rule" "ingress" {
  count             = var.alicloud_enabled ? length(var.firewall_ports) : 0
  security_group_id = alicloud_security_group.this.id
  ip_protocol       = "tcp"
  type              = "ingress"
  port_range        = "${var.firewall_ports[count.index]}/${var.firewall_ports[count.index]}"
}

resource "alicloud_security_group_rule" "egress" {
  count             = var.alicloud_enabled ? 1 : 0
  security_group_id = alicloud_security_group.this.id
  ip_protocol       = "all"
  type              = "egress"
}
