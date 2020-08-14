# Deployment

This is a set of [Terraform](https://www.terraform.io) configurations which create the required infrastructure for HK News server on [AWS Cloud](https://aws.amazon.com).

## Requirements

An active [AWS](https://aws.amazon.com) account with the following permission policies attached:
* AmazonEC2FullAccess: Required for creating HK News VM servers.
* AmazonVPCFullAccess: Required for creating a VPC for HK News servers.
* CloudWatchActionsEC2Access: Required for creating HK News VM servers.

## Instructions

1. Create [AWS](https://aws.amazon.com) access key and secret key for programmatic access. [Instructions](https://docs.aws.amazon.com/general/latest/gr/aws-sec-cred-types.html#access-keys-and-secret-access-keys).
2. (Optional) Create a [Terraform](https://www.terraform.io) credentials file in `$projectDir/src/main/terraform/.terraformrc` if you use a [remote backend](https://www.terraform.io/docs/backends/index.html).
3. Create an account on [Instrumental](https://instrumentalapp.com) for server metrics monitoring.
4. Create an account on [Logz.io](https://logz.io/) for application log tracking.
3. Create a [Terraform](https://www.terraform.io) variables file in `$projectDir/src/main/terraform/secrets.tfvars`:
   ```hcl-terraform
   tag                  = "<A string that identifies the AWS resources created by Terraform>"
   region               = "<AWS region, e.g. us-west-2>"
   zone                 = "<AWS availability zone, e.g. us-west-2a>"
   timezone             = "<A valid tz database time zone, e.g. Asia/Hong_Kong>"
   domain_name          = "<A registered domain name to be associated with the created server, e.g. hknews.dev>"
   key_store_password   = "<The password to access the key store with the domain SSL certificate>"
   instance_type        = "<AWS EC2 instance type, e.g. t3a.micro>"
   storage_size         = "<Server storage size in GB, e.g. 10>"
   access_key           = "<Your AWS access key>"
   secret_key           = "<Your AWS secret key>"
   certbot_email        = "<Your email address used for registering with certbot>"
   instrumental_api_key = "<Your Instrumental API key>"
   logzio_token         = "<Your Logz.io API token>"
   ```
6. Change your working directory to the project root
7. Run [Gradle](https://gradle.org) task:
   ```shell script
   ./gradlew applyTerraform
   ```
