xquery version "1.0";

<doc>
{
	let $name := 
		for $a in doc("Flights-Data.xml")/doc/Airport
		where $a/name = "North Pole"
		return $a/@airId
	for $f in doc("Flights-Data.xml")/doc/Flight
	where $f/source = $name and $f/date = "2005-12-24"
	return <flight><flightId>{data($f/@flightId)}</flightId></flight>
}
</doc>


