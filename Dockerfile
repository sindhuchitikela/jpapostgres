FROM docker.target.com/tap/alpine-adoptopenjdk:11
ADD build/distributions/category.tar /
ENTRYPOINT [ "sh", "-c", "exec /category/bin/category" ]
