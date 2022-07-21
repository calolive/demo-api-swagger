<?php 

$token = '';
$curl = curl_init();

curl_setopt_array($curl, array(
  CURLOPT_URL => 'https://api.sellandsign.com/api/v4/contracts/allinone?start=false',
  CURLOPT_RETURNTRANSFER => true,
  //CURLOPT_SSL_VERIFYPEER => false,
  //CURLOPT_SSL_VERIFYHOST => false,
  CURLOPT_ENCODING => '',
  CURLOPT_MAXREDIRS => 10,
  CURLOPT_TIMEOUT => 0,
  CURLOPT_FOLLOWLOCATION => true,
  CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
  CURLOPT_CUSTOMREQUEST => 'POST',
  CURLOPT_POSTFIELDS => array('contract' => '{
	  "name": "use_case_3_1",
	  "contract_definition_id": 0,
	  "vendor_email": "admin@calindasoftware.com",
	  "message_title": "Essai-Here is your contract",
	  "message_body": "Test-We are pleased to sen you your contract ...",
	  "keep_on_move": false,
	  "perimeters": [
		"pilote"
	  ],
	  "auto_close": 0
	}','recipients' => '{
	  "data": [
		{
		  "civility": "MONSIEUR",
		  "firstname": "alphonse",
		  "lastname": "Allais",
		  "address_1": "Adresse1a",
		  "address_2": "",
		  "postal_code": "75001",
		  "city": "Paris",
		  "cell_phone": "06",
		  "email": "admin+789@calindasoftware.com",
		  "signature_mode": 12
		}
	  ]
	}','pdfparts'=> new CURLFILE(__DIR__.'/resources/smartpacte2.pdf', 'application/pdf')),
  CURLOPT_HTTPHEADER => array(
    'Content-Type: multipart/form-data',
    'j_token: '.$token
  ),
));

$response = curl_exec($curl);
if (curl_errno($curl)) {
	var_dump(curl_errno($curl));
	var_dump(curl_error($curl));
}

$jresponse = json_decode($response);

$curl = curl_init();
curl_setopt_array($curl, array(
 CURLOPT_URL => 'https://api.sellandsign.com/api/v4/contracts/'.$jresponse->contract->contract_id.'/gather/attachment',
 CURLOPT_RETURNTRANSFER => true,
 CURLOPT_ENCODING => '',
 CURLOPT_MAXREDIRS => 10,
 CURLOPT_TIMEOUT => 0,
 CURLOPT_FOLLOWLOCATION => true,
 CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
 CURLOPT_CUSTOMREQUEST => 'POST',
 CURLOPT_POSTFIELDS =>'{
 "key": "Carte identite Recto Verso Permis de conduire Recto Verso 1 justif. de domicile moins 3 mois",
 "to_fill_by_user": true,
 "used_by_contract": false,
 "placeholder": "Carte identite Recto Verso Permis de conduire Recto Verso 1 justif. de domicile moins 3 mois",
 "field_type": "IMAGE",
 "input_filter": "{\\"filetype\\":[\\"PDF,PNG,JPG,JPEG,DOC,DOCX,ODG,ODP,ODS,ODT,PPS,PPT,PPTX,TXT,XLS,XLSX\\"],\\"size\\":\\"50\\",\\"nbfile\\":5}",
 "required": true
 }',
CURLOPT_HTTPHEADER => array(
  'Content-Type: application/json',
  'j_token: '.$token
),
));

$response2 = curl_exec($curl);
if (curl_errno($curl)) {
	var_dump(curl_errno($curl));
	var_dump(curl_error($curl));
}

$curl = curl_init();

curl_setopt_array($curl, array(
  CURLOPT_URL => 'https://api.sellandsign.com/api/v4/contracts/'.$jresponse->contract->contract_id.'/transaction/start',
  CURLOPT_RETURNTRANSFER => true,
  CURLOPT_ENCODING => '',
  CURLOPT_MAXREDIRS => 10,
  CURLOPT_TIMEOUT => 0,
  CURLOPT_FOLLOWLOCATION => true,
  CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
  CURLOPT_CUSTOMREQUEST => 'POST',
  CURLOPT_HTTPHEADER => array(
    'j_token: '.$token
  ),
));

$response3 = curl_exec($curl);
echo json_encode($response3);
curl_close($curl);

?>