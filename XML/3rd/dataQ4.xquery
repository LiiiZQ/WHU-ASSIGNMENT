xquery version "1.0";
(:��ѯ������ΪSanta Claus�ĳ˿͵ĺ���Ŀ�ĵ�:)
<doc>
{
	let $pstNums :=
		for $p in doc("Flights-Data.xml")/doc/Passenger
		where $p/name = "Santa Claus"
		return $p/passportnumber
	
	let $flightIds :=
		for $number in $pstNums ,$reservation in doc("Flights-Data.xml")/doc/Reservation
		where $number = $reservation/passRef
		return $reservation/flightRef
	
	for $f in doc("Flights-Data.xml")/doc/Flight,$fligtId in $flightIds 
	where $f/@flightId = $fligtId 
	return $f/destination
}
</doc>