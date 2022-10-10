<?php
    session_start();
    $_SESSION['base_url_swagger'] = "https://api.sellandsign.com/api/v4";
    $_SESSION['token'] = "";
    $_SESSION['cdi'] = 	0;
    $_SESSION['contractor_id'] = 0;
    $_SESSION['actor_id'] = 0;
    
?>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests">
    </head>
    <body>
        <img src="https://www.doasoft.com/assets/img/zlogoinv60x60.png">
        <br><br>
        <div style="text-align: center">
           <button onclick="send_request('create-contract4.php')">Create contrat</button>
           <button onclick="send_request('oneexpert.php')">Get status</button>
           <select id="generate" onChange="generateOnSelect(this)">
                <option value="" disabled selected>Create token</option>
            </select>
           <button onclick="redirect_to_sign()">Sign contract</button>
           <button onclick="disp_sign_iframe()">Sign contract if</button>
           <button onclick="send_request('get-signed-contract4.php')">Download signed contract</button>      
        </div>
        <br><br>
        <div id="messagediv"></div>
        <div id="iframediv" style="height:800px"></div>
    </body>
    <script>
            var infos = null;
            var contractor = null;
            var lid = 19248 //Put your licence id here
            function send_request(endpoint) {
                var objXMLHttpRequest = new XMLHttpRequest();
                objXMLHttpRequest.onreadystatechange = function() {
                    console.log(objXMLHttpRequest.responseText);
                    if(objXMLHttpRequest.readyState === 4) {
                        if(objXMLHttpRequest.status === 200) {    
                            console.log(objXMLHttpRequest.responseText);                     
                            const myjson = JSON.parse(objXMLHttpRequest.responseText);
                            const iframe = document.getElementById('iframediv');
                            let message = document.getElementById('messagediv');

                            if (endpoint === 'get-signed-contract4.php') {
                                if (myjson.code == 200) {
                                    message.innerHTML =  "";
                                    iframe.innerHTML = `<iframe src="${myjson.pdffile}" height="100%" width="100%"></iframe>`;
                                }
                                else {
                                    message.innerHTML = `<p>request error. Message : ${myjson.pdffile}`;
                                    iframe.innHTML = "";                              
                                }
                            }
                            else {
                                iframe.innerHTML = "";
                                message.innerHTML = `<p>${myjson.response}<p>`;
                                if (endpoint.startsWith('create-token')) {
                                    infos.token = myjson.response;
                                }
                                else if (endpoint.startsWith('create-contract')) {
                                    infos = myjson.response;
                                    fillSelect();
                                }
                            }
                        } else {
                            alert('Error Code: ' +  objXMLHttpRequest.status);
                            alert('Error Message: ' + objXMLHttpRequest.statusText);
                        }
                    }
                }
                objXMLHttpRequest.open('GET', endpoint);
                objXMLHttpRequest.send();               
            }

            function fillSelect() {
                let select = document.getElementById('generate');
                infos.contractors.forEach((c) => {
                    let opt = document.createElement('option');
                    opt.value = c.id;
                    opt.innerHTML = c.lastname;
                    select.appendChild(opt);

                })

            }

            function generateOnSelect(select_object) {
                contractor = select_object.value;
                send_request(`create-token4.php?contractor=${contractor}`);
            }

            function disp_sign_iframe() {
                const message = document.getElementById('messagediv');
                const iframe = document.getElementById('iframediv');
                message.innerHTML = "";
                const encoded = encodeURIComponent(infos.token);
                const url = `https://cloud.sellandsign.com/calinda/s/generic_sign_contract_index.html?l_id=${lid}&direct_contract=${infos.cdi}&cd_id=${infos.cdi}&c_id=${infos.contract_id}&customer_number=${contractor}&page=1&no_ui=true&no_cookie=true&j_token=${encoded}`;                              
                iframe.innerHTML = `<iframe width="1024" height="800" src="${url}"></iframe>`;
            }

            function redirect_to_sign() {
                const success = encodeURIComponent('https://www.google.com');
                const error = encodeURIComponent('https://bing.com');
                const encoded = encodeURIComponent(infos.token);
                //const url = `https://qacss.calindasoftware.com/calinda/sellandsign/#/contract/${infos.contract_id}/sign;c_id=${infos.contract_id};no_ui=true;refback=${success};errorback=${error};j_token=${encoded}`;
                const url = `https://cloud.sellandsign.com/calinda/sellandsign/#/contract/${infos.contract_id}/sign;c_id=${infos.contract_id};no_ui=true;refback=${success};errorback=${error};j_token=${encoded}`;
                console.log(infos.contract_id);
                console.log(contractor);
                //location.href = url;
                window.open(url, '_blank');
            }
        </script>
</html>