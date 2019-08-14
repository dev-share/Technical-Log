#!/bin/sh

# the name of the program/product this curl-bash script lists/installs using
# Bintray as the online repository
PROG=rexray

##
# this curl-install script supports the installation of the latest
# version of $PROG for CentOS, Ubuntu, CoreOS, and Darwin using
# RPMs, DEBs, and tarballs.
#
# to install the latest version of $PROG simply execute:
#
#  curl -sSL https://rexray.io/install | sh
#
# however, this script will also allow users to install specific
# versions of $PROG and even supports the discovery of those versions.
# for example, to list the available $PROG packages use the "list"
# command like so:
#
#  curl -sSL https://rexray.io/install | sh -s -- list
#
# this will emit the following curl commands to demonstrate how to
# install one or more of the available packages, "unstable", "staged",
# and "stable".
#
#    curl -sSL https://rexray.io/install | sh -s -- unstable
#    curl -sSL https://rexray.io/install | sh -s -- staged
#    curl -sSL https://rexray.io/install | sh -s -- stable
#
# again, the "list" command outputs the curl commands to perform
# installations, not how to traverse deeper into the package structure.
# however, it's not hard to do that traversal. for example, to list the
# contents of the "stable" package we'd execute:
#
#  curl -sSL https://rexray.io/install | sh -s -- list stable
#
# the above command would emit somthing similar to:
#
#    curl -sSL https://rexray.io/install | sh -s -- stable 0.2.0
#    curl -sSL https://rexray.io/install | sh -s -- stable latest
#
# as you can see, to traverse the structure, we simply insert the
# command "list" as the first argument after the "-s" flag.
##

REPO=$PROG
BIN_NAME=$PROG

BINTRAY_URL=https://dl.bintray.com/rexray
URL=$BINTRAY_URL/$REPO

SCRIPT_URL=https://rexray.io/install
SCRIPT_CMD="curl -sSL $SCRIPT_URL | sh -s"

if [ "$INSECURE" = "1" ]; then
    INSECURE=-k
fi

sudo() {
    if [ "$(id -u)" -eq "0" ]; then $@; else $SUDO $@; fi
}

is_coreos() {
    grep DISTRIB_ID /etc/lsb-release 2> /dev/null | grep CoreOS 2> /dev/null
}

list() {
    if [ -z "$PKG" ]; then
        echo "$SCRIPT_CMD -- list unstable"
        echo "$SCRIPT_CMD -- list staged"
        echo "$SCRIPT_CMD -- list stable"
    else
        for v in $(curl $INSECURE -sSL "$URL/$PKG/" | \
            grep -o '[[:digit:]]\+\.[[:digit:]]\+\.[[:digit:]]\+\(-[^+/]\+\)\{0,1\}\(\+[[:digit:]]\+\)\{0,1\}' | \
            sort -uV); do
            echo "$SCRIPT_CMD -- $PKG $v"
        done
    fi
}

install() {

    ARCH=$(uname -m)

    if echo $ARCH | grep -iq armv7; then
      ARCH=ARMv7
    elif echo $ARCH | grep -iq armv; then
      ARCH=ARMv8
    fi

    case $ARCH in
    ARMv7)
      ;;
    ARMv8)
      ;;
    x86_64)
      ;;
    *)
      echo "$PROG is not supported on $ARCH platforms"
      exit 1
      ;;
    esac

    if [ "$VERSION" = "latest" ]; then
        VERSION=$(curl $INSECURE -sSL $URL/$PKG/ | \
            grep -o '[[:digit:]]\+\.[[:digit:]]\+\.[[:digit:]]\+\(-[^+/]\+\)\{0,1\}\(\+[[:digit:]]\+\)\{0,1\}' | \
            sort -uV | \
            tail -n 1)
    fi

    OS=$(uname -s)
    URL=$URL/$PKG/$VERSION
    SUDO=$(which sudo)
    BIN_DIR=/usr/bin
    BIN_FILE=$BIN_DIR/$BIN_NAME
    IS_COREOS=$(is_coreos)

    # how to detect the linux distro was taken from http://bit.ly/1JkNwWx
    if [ -e "/etc/redhat-release" -o -e "/etc/redhat-version" ]; then

        FVERSION=$(echo $VERSION | tr '-' '+')
        FILE_NAME=$BIN_NAME-$FVERSION-1.$ARCH.rpm
        URL=$URL/$FILE_NAME

        curl $INSECURE -o $FILE_NAME -sSL $URL
        if [ "$?" -ne "0" ]; then exit $?; fi

        sudo rpm -U --quiet $FILE_NAME > /dev/null
        if [ "$?" -ne "0" ]; then ec=$?; rm -f $FILE_NAME; exit $ec; fi

        rm -f $FILE_NAME

    elif [ "$ARCH" = "x86_64" -a -z "$IS_COREOS" ] && \
         [ -e "/etc/debian-release" -o \
           -e "/etc/debian-version" -o \
           -e "/etc/lsb-release" ]; then

        ARCH=amd64

        FVERSION=$(echo $VERSION | tr '-' '+')
        FILE_NAME=${BIN_NAME}_${FVERSION}-1_${ARCH}.deb
        URL=$URL/$FILE_NAME

        curl $INSECURE -o $FILE_NAME -sSL $URL
        if [ "$?" -ne "0" ]; then exit $?; fi

        sudo dpkg -i $FILE_NAME
        if [ "$?" -ne "0" ]; then ec=$?; rm -f $FILE_NAME; exit $ec; fi

        rm -f $FILE_NAME

    else
        if [ -n "$IS_COREOS" ]; then
            BIN_DIR=/opt/bin
            BIN_FILE=$BIN_DIR/$BIN_NAME
        elif [ "$OS" = "Darwin" ]; then
            BIN_DIR=/usr/local/bin
            BIN_FILE=$BIN_DIR/$BIN_NAME
        fi

        if [ -z "$FILE_NAME" ]; then
            FILE_NAME=$BIN_NAME-$OS-$ARCH-$VERSION.tar.gz
            URL=$URL/$FILE_NAME
        fi

        sudo mkdir -p $BIN_DIR
        if [ "$?" -ne "0" ]; then exit $?; fi

        curl $INSECURE -o $FILE_NAME -sSL $URL
        if [ "$?" -ne "0" ]; then exit $?; fi

        sudo tar xzf $FILE_NAME -C $BIN_DIR
        if [ "$?" -ne "0" ]; then ec=$?; rm -f $FILE_NAME; exit $ec; fi

        rm -f $FILE_NAME

        if [ ! -e "$BIN_FILE" ]; then
            echo "$BIN_FILE does not exist!"
            exit 1
        fi

        sudo chmod 0755 $BIN_FILE && \
            sudo chown 0 $BIN_FILE && \
            sudo chgrp 0 $BIN_FILE
        if [ "$?" -ne "0" ]; then exit $?; fi

        sudo $BIN_FILE install
        if [ "$?" -ne "0" ]; then exit $?; fi
    fi

    echo
    echo "$PROG has been installed to $BIN_FILE"
    echo
    $BIN_FILE version
    echo
}

CMD=$1
if [ "$CMD" = "list" ] || [ "$CMD" = "install" ]; then
    shift
fi

if [ "$CMD" = "list" ]; then
    PKG=$1
    VERSION=$2
    list
else
    PKG=${1:-stable}
    VERSION=${2:-latest}
    FILE_NAME=$3
    install
fi
