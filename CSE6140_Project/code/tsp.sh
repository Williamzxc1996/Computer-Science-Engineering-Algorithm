#!/bin/bash

SEED=0

POSITIONAL=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -inst|--instance)
    INSTANCE="$2"
    shift # past argument
    shift # past value
    ;;
    -alg|--algorithm)
    METHOD="$2"
    shift # past argument
    shift # past value
    ;;
    -time|--time)
    TIME="$2"
    shift # past argument
    shift # past value
    ;;
    -seed|--seed)
    SEED="$2"
    shift # past argument
    shift # past value
    ;;
    *)    # unknown option
    POSITIONAL+=("$1") # save it in an array for later
    shift # past argument
    ;;
esac
done
set -- "${POSITIONAL[@]}" # restore positional parameters


if [[ -n $1 ]]; then
echo "Last line of file specified as non-opt/last argument:"
tail -1 "$1"
fi

if [ $METHOD = "LS2" ];then
    python3 ./simulated_annealing/runAlgo.py $INSTANCE $TIME $SEED
else
    javac com/company/Main.java
    java com.company.Main $INSTANCE $METHOD $TIME $SEED
fi
