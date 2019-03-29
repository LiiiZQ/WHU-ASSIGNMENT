xquery version "1.0";

(:按照2005-12-24日机场繁忙程度（计算进出旅客总数）列出机场（忽略没有旅客进出的机场）、以及其进出旅客总数:)

(:获取某个机场进出旅客总数:)
declare function local:getTotal($date as xs:string, $airport as xs:string)as xs:integer
{
	let $sum := 
		for $b in doc("Flights-Data.xml")/doc/Flight
		where $b/date = $date and ($b/source = $airport or $b/destination = $airport)
		return data($b/seats)

	return sum($sum)
};

<doc>
	{
		for $a in doc/Airport
		let $count := local:getTotal("2005-12-24",data($a/@airId))
		where $count > 0
		order by $count descending
		return <airport>
				<name>{data($a/name)}</name>
				<PassSum>{local:getTotal("2005-12-24",data($a/@airId))}</PassSum>
			</airport>
	}
</doc>
