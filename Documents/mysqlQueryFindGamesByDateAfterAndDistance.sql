use demodb;
Select * from Game left outer join( 
select GameLocations.id,
  (6371 * 
  acos (
  cos(radians(42.02)) * 
 cos(radians(GameLocations.lat)) * 
 cos(
 radians(GameLocations.longt) - radians(-93.646)
 )  +
 sin(radians(42.02)) * 
 sin(radians(GameLocations.lat))
 ) 
  ) 'distance' FROM GameLocations HAVING 'distance' < 10 ORDER BY 'distance') GameLocations
  on (GameLocations.id = Game.gameLocationId) where Game.date > '2019-11-10 17:00:00';	 



 