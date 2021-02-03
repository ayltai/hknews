resource "aws_cloudwatch_dashboard" "this" {
  dashboard_name = "CloudWatch-Default"
  dashboard_body = <<-EOF
{
    "start": "-P1D",
    "widgets": [
        {
            "type": "metric",
            "x": 9,
            "y": 6,
            "width": 9,
            "height": 6,
            "properties": {
                "metrics": [
                    [ { "expression": "SUM(METRICS())", "label": "Consumed write capacity", "id": "e1", "period": 3600, "region": "${var.aws_region}" } ],
                    [ "AWS/DynamoDB", "ConsumedWriteCapacityUnits", "TableName", "Item", { "id": "m1", "visible": false } ],
                    [ "...", "Source", { "id": "m2", "visible": false } ]
                ],
                "view": "timeSeries",
                "stacked": false,
                "region": "${var.aws_region}",
                "title": "DynamoDB (Write)",
                "stat": "Sum",
                "period": 3600,
                "yAxis": {
                    "right": {
                        "min": 0,
                        "showUnits": false
                    },
                    "left": {
                        "min": 0,
                        "showUnits": false
                    }
                },
                "setPeriodToTimeRange": true
            }
        },
        {
            "type": "metric",
            "x": 0,
            "y": 6,
            "width": 9,
            "height": 6,
            "properties": {
                "metrics": [
                    [ { "expression": "SUM(METRICS())", "label": "Consumed read capacity", "id": "e1", "period": 3600, "region": "${var.aws_region}" } ],
                    [ "AWS/DynamoDB", "ConsumedReadCapacityUnits", "TableName", "Item", { "id": "m3", "visible": false } ],
                    [ "...", "Source", { "id": "m1", "visible": false } ]
                ],
                "view": "timeSeries",
                "stacked": false,
                "region": "${var.aws_region}",
                "title": "DynamoDB (Read)",
                "stat": "Sum",
                "period": 3600,
                "yAxis": {
                    "right": {
                        "min": 0,
                        "showUnits": false
                    },
                    "left": {
                        "min": 0,
                        "showUnits": false
                    }
                },
                "setPeriodToTimeRange": true
            }
        },
        {
            "type": "metric",
            "x": 0,
            "y": 0,
            "width": 9,
            "height": 6,
            "properties": {
                "metrics": [
                    [ { "expression": "AVG(METRICS())/1000", "label": "Avg parsing duration x16", "id": "e1", "region": "${var.aws_region}" } ],
                    [ "AWS/Lambda", "Duration", "FunctionName", "AppleDailyParser", { "id": "m1", "visible": false } ],
                    [ "...", "HeadlineParser", { "id": "m2", "visible": false } ],
                    [ "...", "HeadlineRealtimeParser", { "id": "m3", "visible": false } ],
                    [ "...", "HkejParser", { "id": "m4", "visible": false } ],
                    [ "...", "HkejRealtimeParser", { "id": "m5", "visible": false } ],
                    [ "...", "HketParser", { "id": "m6", "visible": false } ],
                    [ "...", "MingPaoParser", { "id": "m7", "visible": false } ],
                    [ "...", "OrientalDailyParser", { "id": "m8", "visible": false } ],
                    [ "...", "OrientalDailyRealtimeParser", { "id": "m9", "visible": false } ],
                    [ "...", "RthkParser", { "id": "m10", "visible": false } ],
                    [ "...", "ScmpParser", { "id": "m11", "visible": false } ],
                    [ "...", "SingPaoParser", { "id": "m12", "visible": false } ],
                    [ "...", "SingTaoParser", { "id": "m13", "visible": false } ],
                    [ "...", "SkyPostParser", { "id": "m14", "visible": false } ],
                    [ "...", "TheStandardParser", { "id": "m15", "visible": false } ],
                    [ "...", "WenWeiPoParser", { "id": "m16", "visible": false } ]
                ],
                "view": "timeSeries",
                "stacked": false,
                "region": "${var.aws_region}",
                "stat": "Average",
                "period": 3600,
                "yAxis": {
                    "left": {
                        "min": 0,
                        "label": "Second",
                        "showUnits": false
                    }
                },
                "title": "Avg parsing duration"
            }
        },
        {
            "type": "metric",
            "x": 9,
            "y": 0,
            "width": 9,
            "height": 6,
            "properties": {
                "metrics": [
                    [ { "expression": "SUM(METRICS())/1000", "label": "Total avg duration", "id": "e1" } ],
                    [ "AWS/Lambda", "Duration", "FunctionName", "Items", { "id": "m1", "visible": false } ],
                    [ "...", "Source", { "yAxis": "left", "id": "m2", "visible": false } ]
                ],
                "view": "timeSeries",
                "stacked": false,
                "region": "${var.aws_region}",
                "stat": "Average",
                "period": 3600,
                "yAxis": {
                    "left": {
                        "min": 0,
                        "label": "Second",
                        "showUnits": false
                    }
                },
                "title": "Avg API duration"
            }
        }
    ]
}
EOF

  depends_on = [
    aws_dynamodb_table.source,
    aws_dynamodb_table.item,
    module.lambda_source,
    module.lambda_item,
    module.lambda_items,
    module.lambda_scheduled,
  ]
}
