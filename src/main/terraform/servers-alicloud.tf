resource "alicloud_instance" "this" {
  count            = var.alicloud_enabled ? 1 : 0
  image_id         = data.alicloud_images.this.images.0.id
  instance_type    = var.alicloud_instance_type
  system_disk_size = var.alicloud_storage_size
  vswitch_id       = alicloud_vswitch.this.id
  key_name         = alicloud_key_pair.this.key_name

  security_groups = [
    alicloud_security_group.this.id,
  ]

  tags = {
    Name = var.tag
  }
}
