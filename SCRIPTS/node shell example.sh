vmstat |awk 'NR==3 {print $14+$13}'
ld_15=`sar -q  2 1 |awk 'NR==5 {print $6}'`
ld_5=`sar -q  2 1 |awk 'NR==5 {print $5}'`

ld_1=`sar -q  1 1 |awk 'NR==5 {print $4}'`
ld_5=`sar -q  1 1 |awk 'NR==5 {print $5}'`
ld_15=`sar -q  1 1 |awk 'NR==5 {print $6}'`
cores=`lscpu | awk '/^CPU\(s\)/ { print $2 }'`
cpuUsage=`sar -u 1 1  |awk 'NR==5 {print $3+$5}'`
clusterId="ocidsasasas"
cpuCeiling=90
curl --location --request POST 'http://localhost:8080/collect' \
--header 'Content-Type: application/json' \
--data-raw '{"vmId":"2",
"ld_1": "${ld_1}",
"ld_5": "13.1",
"ld_15": "12.1",
"cpuUsage": "1.1",
"cores": "22",

}'

curl --location --request POST 'http://172.16.9.243:8080/collect' \
--header 'Content-Type: application/json' \
--data '{"vmId":"1",
"ld_1": '\"${ld_1}\"',
"ld_5": '\"${ld_5}\"',
"ld_15": '\"${ld_15}\"',
"clusterId": '\"${clusterId}\"',
"cpuUsage": '\"${cpuUsage}\"',
"qweight": '\"${qweight}\"',
"mincore": '\"${mincore}\"',
"maxcore": '\"${maxcore}\"',
"cpuCeiling": '\"${cpuCeiling}\"',
"cores": '\"${cores}\"'}'

