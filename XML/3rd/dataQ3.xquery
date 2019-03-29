xquery version "1.0";

(:����2005-12-24�ջ�����æ�̶ȣ���������ÿ��������г�����������û���ÿͽ����Ļ��������Լ�������ÿ�����:)

(:��ȡĳ�����������ÿ�����:)
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
