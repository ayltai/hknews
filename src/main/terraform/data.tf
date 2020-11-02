data "aws_ami" "this" {
  filter {
    name = "name"

    values = [
      var.ami_filter,
    ]
  }

  filter {
    name = "virtualization-type"

    values = [
      "hvm",
    ]
  }

  owners = [
    var.ami_owner,
  ]

  most_recent = true
}

data "alicloud_images" "this" {
  name_regex   = var.alicloud_image_filter
  architecture = "x86_64"
  most_recent  = true
  owners       = var.alicloud_image_owner
}
