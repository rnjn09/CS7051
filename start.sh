if [ $# != 1 ]
then
echo
echo "!!! Wrong format !!!"
echo "Usage: $0 <portnumber>"
echo
exit 0
fi

echo "SERVER UP"
java Server $1