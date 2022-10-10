<?php

require_once 'vendor/autoload.php';
use GuzzleHttp\Client;
use GuzzleHttp\Exception\RequestException;

session_start();
$base_url = $_SESSION['base_url'];
$token = $_SESSION['token'];
$cdi = $_SESSION['cdi'];
$contract_id = $_SESSION['contract_id'];

$client = new Client([
    // You can set any number of default request options.
    'timeout'  => 30.0,
]);

try {
// //Contract autoclose ------------------------------------------------------
    $resp_autoclose = $client->request('POST', "$base_url/calinda/hub/selling/model/contract/update?action=updateContractAutoCloseMode", [
        'headers' => [
            'j_token' => $token,
            'Content_Type' => 'application/json',
            'Accept' => 'application/json'

        ],
        'body' => "{\"auto_close\" : 1 ,\"id\" : $contract_id}"
    ]);

    $resp_json = array('code' => $resp_autoclose->getStatusCode(), 'response' => $resp_autoclose->getBody()->getContents());
    echo json_encode($resp_json);
}
catch (RequestException $e) {
    $resp_json = array('code' => 400, 'response' => $e->getMessage());   
    echo json_encode($resp_json);
}

?>