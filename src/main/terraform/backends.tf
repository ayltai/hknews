terraform {
  backend "remote" {
    hostname     = "app.terraform.io"
    organization = "hknews"

    workspaces {
      prefix = "aws-"
    }
  }
}
