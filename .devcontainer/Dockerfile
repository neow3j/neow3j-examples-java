# See here for image contents: https://github.com/microsoft/vscode-dev-containers/blob/main/containers/java/.devcontainer/base.Dockerfile
# [Choice] Java version (use -bullseye variants on local arm64/Apple Silicon): 8, 11, 17, 8-bullseye, 11-bullseye, 17-bullseye, 8-buster, 11-buster, 17-buster
ARG VARIANT=11-bullseye

FROM mcr.microsoft.com/dotnet/sdk:6.0 as dotnet-builder

FROM mcr.microsoft.com/vscode/devcontainers/java:${VARIANT}

# [Option] Install Maven
ARG INSTALL_MAVEN="false"
ARG MAVEN_VERSION=""
# [Option] Install Gradle
ARG INSTALL_GRADLE="false"
ARG GRADLE_VERSION=""
RUN if [ "${INSTALL_MAVEN}" = "true" ]; then su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven \"${MAVEN_VERSION}\""; fi \
    && if [ "${INSTALL_GRADLE}" = "true" ]; then su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install gradle \"${GRADLE_VERSION}\""; fi

# [Choice] Node.js version: none, lts/*, 16, 14, 12, 10
ARG NODE_VERSION="none"
RUN if [ "${NODE_VERSION}" != "none" ]; then su vscode -c "umask 0002 && . /usr/local/share/nvm/nvm.sh && nvm install ${NODE_VERSION} 2>&1"; fi

# [Optional] Uncomment this section to install additional OS packages.
# RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
#     && apt-get -y install --no-install-recommends <your-package-list-here>

# [Optional] Uncomment this line to install global node packages.
# RUN su vscode -c "source /usr/local/share/nvm/nvm.sh && npm install -g <your-package-here>" 2>&1

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

# Set-up user 'yaky'
RUN groupadd --gid 1002 yaky && \
    useradd --create-home --shell /bin/bash --uid 1002 --gid 1002 yaky

# default UID and GID for the 'yaky' user
ENV USER_UID=1002
ENV USER_GID=1002

# Copy dotnet
COPY --from=dotnet-builder /usr/share/dotnet /usr/share/dotnet

RUN ln -s /usr/share/dotnet/dotnet /usr/bin/dotnet && \
    # Do not erase this.
    # See: https://github.com/OmniSharp/omnisharp-vscode/issues/3077
    # It seems that Omnisharp still searches for /usr/local/bin/dotnet and, if not set,
    # the extension triggers to install it again in the docker container when the code-server runs
    # (which generates lots of unnecessary CPU load).
    ln -s /usr/share/dotnet/dotnet /usr/local/bin/dotnet

COPY --from=dotnet-builder /usr/share/powershell/ /usr/share/powershell/

RUN ln -s /usr/share/powershell/pwsh /usr/bin/pwsh && \
    chmod 755 /usr/share/powershell/pwsh

#####################

USER yaky

ENV \
    #############################
    # Dotnet: Env vars:
    #############################
    #
    # Unset ASPNETCORE_URLS from aspnet base image
    ASPNETCORE_URLS="" \
    # Do not generate certificate
    DOTNET_GENERATE_ASPNET_CERTIFICATE=false \
    # Do not show first run text
    DOTNET_NOLOGO=true \
    # SDK version
    DOTNET_SDK_VERSION=6.0.201 \
    # Enable correct mode for dotnet watch (only mode supported in a container)
    DOTNET_USE_POLLING_FILE_WATCHER=true \
    # Unset Logging__Console__FormatterName from aspnet base image
    Logging__Console__FormatterName="" \
    # Skip extraction of XML docs - generally not useful within an image/container - helps performance
    NUGET_XMLDOC_MODE=skip \
    # PowerShell telemetry for docker image usage
    POWERSHELL_DISTRIBUTION_CHANNEL=PSDocker-DotnetSDK-Ubuntu-22.04 \
    #
    # Enable detection of running in a container
    DOTNET_RUNNING_IN_CONTAINER=true \
    # Disable Telemetry
    DOTNET_CLI_TELEMETRY_OPTOUT=true

ENV \
    #############################
    # Dotnet:
    #############################
    PATH=$HOME/.dotnet/tools:$PATH
