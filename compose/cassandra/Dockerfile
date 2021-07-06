FROM cassandra:3.11.10
RUN sed -i 's/^authenticator.\+$/authenticator: PasswordAuthenticator/g' /etc/cassandra/cassandra.yaml
RUN sed -i 's/^authorizer.\+$/authorizer: CassandraAuthorizer/g' /etc/cassandra/cassandra.yaml