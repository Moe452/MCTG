@echo off

REM --------------------------------------------------
REM Monster Trading Cards Game
REM --------------------------------------------------
title Monster Trading Cards Game
echo CURL Testing for Monster Trading Cards Game
echo.

REM --------------------------------------------------
echo 1) Create Users (Registration)
REM Create User
curl -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}"
echo.
curl -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"altenhof\", \"Password\":\"markus\"}"
echo.
curl -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"admin\",    \"Password\":\"istrator\"}"
echo.

echo should fail:
curl -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}"
echo.
curl -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"different\"}"
echo. 
echo.

REM --------------------------------------------------
echo 2) Login Users
curl -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}"
echo.
curl -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"altenhof\", \"Password\":\"markus\"}"
echo.
curl -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"admin\",    \"Password\":\"istrator\"}"
echo.

echo should fail:
curl -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"different\"}"
echo.
echo.

REM --------------------------------------------------
echo 3) create packages (done by "admin")
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":5, \"Name\":\"WaterGoblin\", \"Damage\": 10.0}, {\"Id\":6, \"Name\":\"Dragon\", \"Damage\": 50.0}, {\"Id\":7, \"Name\":\"WaterSpell\", \"Damage\": 20.0}, {\"Id\":8, \"Name\":\"Ork\", \"Damage\": 45.0}, {\"Id\":9, \"Name\":\"FireSpell\",    \"Damage\": 25.0}]"
echo.																																																																																		 				    
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":30, \"Name\":\"WaterGoblin\", \"Damage\":  9.0}, {\"Id\":33, \"Name\":\"Dragon\", \"Damage\": 55.0}, {\"Id\":34, \"Name\":\"WaterSpell\", \"Damage\": 21.0}, {\"Id\":3, \"Name\":\"Ork\", \"Damage\": 55.0}, {\"Id\":35, \"Name\":\"WaterSpell\",   \"Damage\": 23.0}]"
echo.																																																																																		 				    
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":30, \"Name\":\"WaterGoblin\", \"Damage\":  9.0}, {\"Id\":33, \"Name\":\"Dragon\", \"Damage\": 55.0}, {\"Id\":34, \"Name\":\"WaterSpell\", \"Damage\": 21.0}, {\"Id\":3, \"Name\":\"Ork\", \"Damage\": 55.0}, {\"Id\":35, \"Name\":\"RegularSpell\",   \"Damage\": 23.0}]"
echo.																																																																																		 				    
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":66, \"Name\":\"WaterGoblin\", \"Damage\": 10.0}, {\"Id\":77, \"Name\":\"Dragon\", \"Damage\": 50.0}, {\"Id\":76, \"Name\":\"WaterSpell\", \"Damage\": 20.0}, {\"Id\":88, \"Name\":\"Ork\", \"Damage\": 45.0}, {\"Id\":98, \"Name\":\"WaterSpell\",   \"Damage\": 25.0}]"
echo.																																																																																		 				    
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":92, \"Name\":\"WaterGoblin\", \"Damage\":  9.0}, {\"Id\":93, \"Name\":\"Dragon\", \"Damage\": 55.0}, {\"Id\":94, \"Name\":\"WaterSpell\", \"Damage\": 21.0}, {\"Id\":95, \"Name\":\"Ork\", \"Damage\": 55.0}, {\"Id\":86, \"Name\":\"FireSpell\",    \"Damage\": 23.0}]"
echo.																																																																																		 				    
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":101, \"Name\":\"WaterGoblin\", \"Damage\": 11.0}, {\"Id\":102, \"Name\":\"Dragon\", \"Damage\": 70.0}, {\"Id\":103, \"Name\":\"WaterSpell\", \"Damage\": 22.0}, {\"Id\":104, \"Name\":\"Ork\", \"Damage\": 40.0}, {\"Id\":105, \"Name\":\"RegularSpell\", \"Damage\": 28.0}]"
echo.
echo.

REM --------------------------------------------------
echo 4) acquire packages kienboec
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d ""
echo.
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d ""
echo.
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d ""
echo.
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d ""
echo.
echo should fail (no money):
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d ""
echo.
echo.

REM --------------------------------------------------
echo 5) acquire packages altenhof
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d ""
echo.
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d ""
echo.
echo should fail (no package):
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d ""
echo.
echo.

REM --------------------------------------------------
echo 6) add new packages
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":200, \"Name\":\"WaterGoblin\", \"Damage\": 10.0}, {\"Id\":201, \"Name\":\"RegularSpell\", \"Damage\": 50.0}, {\"Id\":202, \"Name\":\"Knight\", \"Damage\": 20.0}, {\"Id\":203, \"Name\":\"RegularSpell\", \"Damage\": 45.0}, {\"Id\":204, \"Name\":\"FireElf\",    \"Damage\": 25.0}]"
echo.
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":233, \"Name\":\"WaterGoblin\", \"Damage\": 10.0}, {\"Id\":234, \"Name\":\"RegularSpell\", \"Damage\": 50.0}, {\"Id\":235, \"Name\":\"Knight\", \"Damage\": 20.0}, {\"Id\":236, \"Name\":\"RegularSpell\", \"Damage\": 45.0}, {\"Id\":237, \"Name\":\"FireElf\",    \"Damage\": 30.0}]"
echo.
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":251, \"Name\":\"WaterGoblin\", \"Damage\": 10.0}, {\"Id\":252, \"Name\":\"RegularSpell\", \"Damage\": 50.0}, {\"Id\":253, \"Name\":\"Knight\", \"Damage\": 23.0}, {\"Id\":254, \"Name\":\"RegularSpell\", \"Damage\": 45.0}, {\"Id\":256, \"Name\":\"FireElf\",    \"Damage\": 27.0}]"
echo.
echo.

REM --------------------------------------------------
echo 7) acquire newly created packages altenhof
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d ""
echo.
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d ""
echo.
echo should fail (no money):
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d ""
echo.
echo.

REM --------------------------------------------------
echo 8) show all acquired cards kienboec
curl -X GET http://localhost:10001/cards --header "Authorization: Basic kienboec-mtcgToken"
echo should fail (no token)
curl -X GET http://localhost:10001/cards 
echo.
echo.

REM --------------------------------------------------
echo 9) show all acquired cards altenhof
curl -X GET http://localhost:10001/cards --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 10) show unconfigured deck
curl -X GET http://localhost:10001/deck --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/deck --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 11) configure deck
curl -X PUT http://localhost:10001/deck --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d "[\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\"]"
echo.
curl -X GET http://localhost:10001/deck --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X PUT http://localhost:10001/deck --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d "[\"aa9999a0-734c-49c6-8f4a-651864b14e62\", \"d6e9c720-9b5a-40c7-a6b2-bc34752e3463\", \"d60e23cf-2238-4d49-844f-c7589ee5342e\", \"02a9c76e-b17d-427f-9240-2dd49b0d3bfd\"]"
echo.
curl -X GET http://localhost:10001/deck --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.
echo should fail and show original from before:
curl -X PUT http://localhost:10001/deck --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d "[\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\"]"
echo.
curl -X GET http://localhost:10001/deck --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.
echo should fail ... only 3 cards set
curl -X PUT http://localhost:10001/deck --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d "[\"aa9999a0-734c-49c6-8f4a-651864b14e62\", \"d6e9c720-9b5a-40c7-a6b2-bc34752e3463\", \"d60e23cf-2238-4d49-844f-c7589ee5342e\"]"
echo.


REM --------------------------------------------------
echo 12) show configured deck 
curl -X GET http://localhost:10001/deck --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/deck --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 13) show configured deck different representation
echo kienboec
curl -X GET http://localhost:10001/deck?format=plain --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo.
echo altenhof
curl -X GET http://localhost:10001/deck?format=plain --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 14) edit user data
echo.
curl -X GET http://localhost:10001/users/kienboec --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/users/altenhof --header "Authorization: Basic altenhof-mtcgToken"
echo.
curl -X PUT http://localhost:10001/users/kienboec --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d "{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}"
echo.
curl -X PUT http://localhost:10001/users/altenhof --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d "{\"Name\": \"Altenhofer\", \"Bio\": \"me codin...\",  \"Image\": \":-D\"}"
echo.
curl -X GET http://localhost:10001/users/kienboec --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/users/altenhof --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.
echo should fail:
curl -X GET http://localhost:10001/users/altenhof --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/users/kienboec --header "Authorization: Basic altenhof-mtcgToken"
echo.
curl -X PUT http://localhost:10001/users/kienboec --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d "{\"Name\": \"Hoax\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}"
echo.
curl -X PUT http://localhost:10001/users/altenhof --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d "{\"Name\": \"Hoax\", \"Bio\": \"me codin...\",  \"Image\": \":-D\"}"
echo.
curl -X GET http://localhost:10001/users/someGuy  --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 15) stats
curl -X GET http://localhost:10001/stats --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/stats --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 16) scoreboard
curl -X GET http://localhost:10001/score --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 17) battle
start /b "kienboec battle" curl -X POST http://localhost:10001/battles --header "Authorization: Basic kienboec-mtcgToken"
start /b "altenhof battle" curl -X POST http://localhost:10001/battles --header "Authorization: Basic altenhof-mtcgToken"
ping localhost -n 10 >NUL 2>NUL

REM --------------------------------------------------
echo 18) Stats 
echo kienboec
curl -X GET http://localhost:10001/stats --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo altenhof
curl -X GET http://localhost:10001/stats --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 19) scoreboard
curl -X GET http://localhost:10001/score --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 20) trade
echo check trading deals
curl -X GET http://localhost:10001/tradings --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo create trading deal
curl -X POST http://localhost:10001/tradings --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d "{\"Id\": \"6cd85277-4590-49d4-b0cf-ba0a921faad0\", \"CardToTrade\": \"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\", \"Type\": \"monster\", \"MinimumDamage\": 15}"
echo.
echo check trading deals
curl -X GET http://localhost:10001/tradings --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/tradings --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo delete trading deals
curl -X DELETE http://localhost:10001/tradings/6cd85277-4590-49d4-b0cf-ba0a921faad0 --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 21) check trading deals
curl -X GET http://localhost:10001/tradings  --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X POST http://localhost:10001/tradings --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d "{\"Id\": \"6cd85277-4590-49d4-b0cf-ba0a921faad0\", \"CardToTrade\": \"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\", \"Type\": \"monster\", \"MinimumDamage\": 15}"
echo check trading deals
curl -X GET http://localhost:10001/tradings  --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/tradings  --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo try to trade with yourself (should fail)
curl -X POST http://localhost:10001/tradings/6cd85277-4590-49d4-b0cf-ba0a921faad0 --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d "\"4ec8b269-0dfa-4f97-809a-2c63fe2a0025\""
echo.
echo try to trade 
echo.
curl -X POST http://localhost:10001/tradings/6cd85277-4590-49d4-b0cf-ba0a921faad0 --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d "\"951e886a-0fbf-425d-8df5-af2ee4830d85\""
echo.
curl -X GET http://localhost:10001/tradings --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/tradings --header "Authorization: Basic altenhof-mtcgToken"
echo.

REM --------------------------------------------------
echo end...

REM this is approx a sleep 
ping localhost -n 100 >NUL 2>NUL
@echo on
