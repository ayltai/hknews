resource "random_string" "random" {
  length  = 3
  number  = false
  upper   = false
  special = false
}

resource "local_file" "ansible_playbook" {
  count             = var.aws_enabled || var.alicloud_enabled ? 1 : 0
  filename          = "ansible_playbook.sh"
  file_permission   = "0755"
  sensitive_content = <<EOF
ANSIBLE_HOST_KEY_CHECKING=False \
ANSIBLE_ROLES_PATH=${path.cwd}/../ansible/.roles \
ansible-playbook --extra-vars "\
timezone=${var.timezone} \
ip_address=${var.aws_enabled ? aws_eip.this.0.public_ip : alicloud_eip.this.0.ip_address} \
domain_name=${var.domain_name} \
key_store_password=${var.key_store_password} \
certbot_email=${var.certbot_email} \
instrumental_api_key=${var.instrumental_api_key} \
logzio_token=${var.logzio_token} \
random=${random_string.random.result}\
" -i inventory.ini ${path.cwd}/../ansible/playbook.yml
EOF
}

resource "null_resource" "deployment_aws" {
  count = var.aws_enabled ? 1 : 0

  depends_on = [
    aws_instance.this,
    aws_eip.this,
  ]

  triggers = {
    always_run = timestamp()
  }

  provisioner "remote-exec" {
    inline = [
      "sudo apt update",
      "sudo apt -qq install python3 -y",
    ]

    connection {
      agent       = false
      timeout     = var.timeout
      host        = aws_eip.this.0.public_ip
      private_key = tls_private_key.this.private_key_pem
      user        = var.username
    }
  }

  provisioner "local-exec" {
    command = <<EOF
sleep 50;
>inventory.ini;
echo "[hknews]" | tee -a inventory.ini;
echo "${aws_eip.this.0.public_ip} ansible_user=${var.username} ansible_ssh_private_key_file=${path.cwd}/${var.tag}.pem" | tee -a inventory.ini
ansible-galaxy install --roles-path ${path.cwd}/../ansible/.roles -r ${path.cwd}/../ansible/requirements.yml
./ansible_playbook.sh
EOF
  }
}

resource "null_resource" "deployment_alicloud" {
  count = var.alicloud_enabled ? 1 : 0

  depends_on = [
    alicloud_instance.this,
    alicloud_eip.this,
  ]

  triggers = {
    always_run = timestamp()
  }

  provisioner "remote-exec" {
    inline = [
      "sudo apt update",
      "sudo apt -qq install python3 -y",
    ]

    connection {
      agent       = false
      timeout     = var.timeout
      host        = alicloud_eip.this.0.ip_address
      private_key = tls_private_key.this.private_key_pem
      user        = var.username
    }
  }

  provisioner "local-exec" {
    command = <<EOF
sleep 50;
>inventory.ini;
echo "[hknews]" | tee -a inventory.ini;
echo "${alicloud_eip.this.0.ip_address} ansible_user=${var.username} ansible_ssh_private_key_file=${path.cwd}/${var.tag}.pem" | tee -a inventory.ini
ansible-galaxy install --roles-path ${path.cwd}/../ansible/.roles -r ${path.cwd}/../ansible/requirements.yml
./ansible_playbook.sh
EOF
  }
}
