xquery version "1.0";

(:查询2005-12-24日最繁忙（计算出发和到达航班）的机场、以及其进出航班总数:)
(:机场进出航班总数:)
declare function local:getTotal($date as xs:string, $airport as xs:string)as xs:integer
{
	let $count := 
		for $b in doc("Flights-Data.xml")/doc/Flight
		where $b /date = $date and ($b/source = $airport or $b/destination = $airport)
		return $b 
	
	return count($count)
};

(:最繁忙机场:)
declare function local:getMax($date as xs:string)as xs:string
{
	let $c :=
		for $a in doc("Flights-Data.xml")/doc/Airport
		let $count as xs:integer := local:getTotal($date ,$a/@airId)
		order by $count descending
		return data($a/name)
		
	return $c[1]
};


<doc>
	<mostBusyAirport>{local:getMax("2005-12-24")}</mostBusyAirport>
	
	{
		let $id := 
			let $n := local:getMax("2005-12-24")
			for $a in doc("Flights-Data.xml")/doc/Airport
			where $a/name = $n
			return $a/@airId
		return <maxFlightsCount>{data(local:getTotal("2005-12-24",$id))}</maxFlightsCount>
	}
	
</doc>
