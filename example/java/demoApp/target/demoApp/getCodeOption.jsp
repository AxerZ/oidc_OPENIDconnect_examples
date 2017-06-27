<%-- 
    Document   : go
    Created on : 2017/6/16, 上午 11:48:15
    Author     : igogo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="https://unpkg.com/vue"></script>
        <title>JSP Page</title>
    </head>
    <body>
        <header>
            <style type="text/css">
                .modal-mask {
                    position: fixed;
                    z-index: 9998;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    background-color: rgba(0, 0, 0, .5);
                    display: table;
                    transition: opacity .3s ease;
                }

                .modal-wrapper {
                    display: table-cell;
                    vertical-align: middle;
                }

                .modal-container {
                    width: 400px;
                    height: 120px;
                    margin: 0px auto;
                    padding: 20px 30px;
                    background-color: #fff;
                    border-radius: 2px;
                    box-shadow: 0 2px 8px rgba(0, 0, 0, .33);
                    transition: all .3s ease;
                    font-family: Helvetica, Arial, sans-serif;
                }

                .modal-header h4 {
                    margin-top: 0;
                    color: #42b983;
                }

                .modal-body {
                    margin: 20px 0;
                }
                /*http://www.bestcssbuttongenerator.com/*/
                .modal-default-button {
                    float: right;
                    background: #3498db;
                    background-image: -webkit-linear-gradient(top, #3498db, #2980b9);
                    background-image: -moz-linear-gradient(top, #3498db, #2980b9);
                    background-image: -ms-linear-gradient(top, #3498db, #2980b9);
                    background-image: -o-linear-gradient(top, #3498db, #2980b9);
                    background-image: linear-gradient(to bottom, #3498db, #2980b9);
                    -webkit-border-radius: 10px;
                    -moz-border-radius: 10px;
                    border-radius: 10px;
                    font-family: Georgia;
                    color: #ffffff;
                    font-size: 15px;
                    padding: 8px 9px 9px 8px;
                    border: solid #1f628d 1px;
                    text-decoration: none;
                }
                /*
                     * The following styles are auto-applied to elements with
                     * transition="modal" when their visibility is toggled
                     * by Vue.js.
                     *
                     * You can easily play with the modal transition by editing
                     * these styles.
                     */

                .modal-enter {
                    opacity: 0;
                }

                .modal-leave-active {
                    opacity: 0;
                }

                .modal-enter .modal-container,
                .modal-leave-active .modal-container {
                    -webkit-transform: scale(1.1);
                    transform: scale(1.1);
                }
            </style>
        </header>

        <!-- template for the modal component -->
        <script type="text/x-template" id="modal-template">
            <transition name="modal">
            <div class="modal-mask">
            <div class="modal-wrapper">
            <div class="modal-container">

            <div class="modal-header">
            <slot name="header">
                header string
            </slot>
            </div>

             <div class="modal-body">
            <slot name="body">
                body string
            </slot>
            </div>


            <div class="modal-footer">
            <slot name="footer">

            <button class="modal-default-button" @click="$emit('close','yes')">
            YES
            </button>

            <button class="modal-default-button" @click="$emit('close','no')">
            NO
            </button>
            </slot>
            </div>
            </div>
            </div>
            </div>
            </transition>
        </script>

        <!-- app -->
        <div id="app">

            <!-- use the modal component, pass in the prop -->
            <modal v-if="showModal" @close="say">
                <!--
                  you can use custom content here to overwrite
                  default content
                -->
                <h4 slot="header">成功取得Auth Code</h4>
                <p slot ="body">按YES 自動取得access token<br/>
                    按NO,我將手動操作 </p>
            </modal>
        </div>


        <script>
// register modal component
Vue.component('modal', {
    template: '#modal-template'

})

// start app
new Vue({
    el: '#app',
    data: {
        showModal: true
    },
    methods: {
        say: function (msg) {
            console.log(msg);
            this.showModal = false;
            window.location.href = 'callback?getCodeOption=' + msg;
        }
    }
})
        </script>
    </body>
</html>
