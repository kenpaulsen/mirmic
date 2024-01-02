
# To setup a ECS Cluster

1. Goto AWS Console and click "Create Cluster"
1. Give it a name, pick Fargate, use container insights (optional)
    1. Note: I had to add more permissions under `Account Settings` if I wanted to be able to tag or use container insights.

# To Create a Service

1. Click Create
1. Choose `Launch Type`
1. Choose `FARGATE`
1. Choose `LATEST`
1. Choose `Service`
1. Create a Task Definition (or choose an existing one)
    1. Choose a name (mirmic)
    1. Choose Linux/ARM64
    1. Choose CPU / RAM (0.5 / 2 GB)
    1. Type Container 1 Details
        1. Paste URI from ECR (i.e. 762393489277.dkr.ecr.us-west-2.amazonaws.com/paulsen)
    1. Choose Storage Settings
        1. EFS
        1. Volume name: data
        1. File System ID (create new in Amazon EFS Console, if not yet created)
            1. name it, customize settings, single zone, us-west-2c, enable backups (configure log ), etc.
            1. Mount it to /mnt/data
1. Finish creating `Service`
    1. Search for TaskDefinition (i.e. mirmic)
    1. Name service Tickets
    1. Choose only the us-west-2c subnet (to match our volume)
    1. Leave on public IP
1. This then failed w/ error: `ResourceInitializationError: failed to invoke EFS utils commands to set up EFS volumes: stderr: Mount attempt 1/3 failed due to timeout after 15 sec, wait 0 sec before next attempt. Mount attempt 2/3 failed due to timeout after 15 sec, wait 0 sec before next attempt. b'mount.nfs4: Connection timed out' : unsuccessful EFS utils command execution; code: 32`. Which seems to be solvable by
    1. Opening port 2049 inbound on the security group on the network interface and task definition.
    1. Security group: add an inbound rule to with type NFS and port 2049
    1. Subnet: Ensure your network is able to connect to your EFS. you could check detail in EFS's networking
1. Create log retention policy on log groups.

## NOTE: Without EFS it is working just fine... FIXME: Solve EFS config
