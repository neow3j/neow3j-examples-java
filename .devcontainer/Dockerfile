# [Choice] Java version (use -bullseye variants on local arm64/Apple Silicon):
# 8, 11, 17, 8-bullseye, 11-bullseye, 17-bullseye, 8-buster, 11-buster, 17-buster
ARG VARIANT=0-17-jdk-bullseye

FROM mcr.microsoft.com/devcontainers/java:${VARIANT}

# install dependencies
RUN apt-get update -y && \
    apt-get install -y --no-install-recommends \
        curl \
        gnupg \
        apt-transport-https \
        ca-certificates \
        && \
    curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add - && \
    apt-get update -y && \
    apt-get install -y --no-install-recommends \
        # Essentials:
        make \
        vim \
        git \
        curl \
        unzip \
        jq \
        build-essential \
        pkg-config \
        apt-transport-https \
        ca-certificates \
        software-properties-common \
        gnupg \
        # Neo3: Node dependencies:
        libleveldb-dev \
        sqlite3 \
        # .NET Core SDK dependencies:
        libc6 \
        libgcc1 \
        libgssapi-krb5-2 \
        libstdc++6 \
        zlib1g \
        # Neo Blockchain Toolkit dependencies:
        libsnappy-dev \
        libc6-dev \
        librocksdb-dev \
        && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

#####################

ENV \
    #############################
    # Dotnet: Env vars:
    #############################
    #
    # Do not generate certificate
    DOTNET_GENERATE_ASPNET_CERTIFICATE=false \
    # Do not show first run text
    DOTNET_NOLOGO=true \
    # Enable correct mode for dotnet watch (only mode supported in a container)
    DOTNET_USE_POLLING_FILE_WATCHER=true \
    # Unset Logging__Console__FormatterName from aspnet base image
    Logging__Console__FormatterName="" \
    # Skip extraction of XML docs - generally not useful within an image/container - helps performance
    NUGET_XMLDOC_MODE=skip \
    #
    # Enable detection of running in a container
    DOTNET_RUNNING_IN_CONTAINER=true \
    # Disable Telemetry
    DOTNET_CLI_TELEMETRY_OPTOUT=true

ENV DOTNET_ROOT=/dotnet
ENV PATH=$DOTNET_ROOT:$DOTNET_ROOT/tools:$PATH

RUN curl -sSL https://dot.net/v1/dotnet-install.sh | \
    bash /dev/stdin --version 7.0.202 --install-dir $DOTNET_ROOT

RUN curl -sSL https://dot.net/v1/dotnet-install.sh | \
    bash /dev/stdin --runtime dotnet --version 6.0.15 --install-dir $DOTNET_ROOT

RUN curl -sSL https://dot.net/v1/dotnet-install.sh | \
    bash /dev/stdin --runtime aspnetcore --version 6.0.15 --install-dir $DOTNET_ROOT