# akka-cluster-distributed-data
Playing with Akka cluster distributed data and Akka typed. I made this project in order to learn the (relatively) new Akka typed system and also the Akka cluster distributed data.

It's actually combining the examples of Akka typed, Akka cluster distributed data and Akka HTTP. Nothing special here, but I kept this for future reference. The goal is to have data from one instance read from the other one.

## Compiling
Just do `sbt/compile`, or pack it using `sbt/package`. There are several package options. I used the standard sbt-native-packager plugin.

## Running
Start several servers. One server should have access to any port on the other one. Akka cluster uses some ports for the communication. Then run the two or more process with the proper configuration.
### Example
Let's assume we have 2 servers with ips: 10.128.0.14, 10.128.0.15.

On server 10.128.0.14:
```
sudo ./akka-cluster-distributed-data -Dmy-app.bind-port=80  -Dakka.remote.artery.canonical.hostname=10.128.0.14 -Dakka.cluster.seed-nodes.1=akka://ClusterSystem@10.128.0.14:2551 -Dakka.cluster.seed-nodes.2=akka://ClusterSystem@10.128.0.15:2551
```
On server 10.128.0.15:
```
sudo ./akka-cluster-distributed-data -Dmy-app.bind-port=80  -Dakka.remote.artery.canonical.hostname=10.128.0.15 -Dakka.cluster.seed-nodes.1=akka://ClusterSystem@10.128.0.14:2551 -Dakka.cluster.seed-nodes.2=akka://ClusterSystem@10.128.0.15:2551
```

## Testing
First run:

```curl -X POST -H "Content-Type: application/json" -d '{"something": "blabla"}' http://<some_instance>/api/resource```

And then run:

```curl http://<another_instance>/api/resource```

Should print:

{"something": "blabla"}
