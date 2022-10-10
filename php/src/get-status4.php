<?php

require_once 'vendor/autoload.php';
use GuzzleHttp\Client;
use GuzzleHttp\Exception\RequestException;

session_start();
$base_url = $_SESSION['base_url_swagger'];
$token = $_SESSION['token'];
$cdi = $_SESSION['cdi'];
$contract_id = $_SESSION['contract_id'];

$client = new Client([
    // You can set any number of default request options.
    'timeout'  => 30.0,
]);

try {
    $resp_info = $client->request('GET', "$base_url/contracts/$contract_id", [
        'headers' => [
            'j_token' => $token,
            'Accept' => 'application/json'
        ]
        ]);
    $resp_json = array('code' => $resp_info->getStatusCode(), 'response' => $resp_info->getBody()->getContents());
    echo json_encode($resp_json);
}
// try {
// //Get contract information --------------------------------------------
//     $resp_info = $client->request('GET', "$base_url/calinda/hub/selling/model/contract/read", [
//         'query' => [
//             'action' => 'getContract',
//             'contract_id' => $contract_id,
//         ],
//         'headers' => [

//             'j_token' => $token,
//             'Accept' => 'application/json'

//         ]
//     ]);

//     $resp_json = array('code' => $resp_info->getStatusCode(), 'response' => $resp_info->getBody()->getContents());
//     echo json_encode($resp_json);
// }
catch (RequestException $e) {
    $resp_json = array('code' => 400, 'response' => $e->getMessage());   
    echo json_encode($resp_json);
}

?>