//
//  ViewController.swift
//  Cobon Reviews
//
//  Created by Muhammad Hewedy on 7/3/16.
//  Copyright © 2016 Muhammad Hewedy. All rights reserved.
//

import UIKit
import SVProgressHUD

class ViewController: UIViewController, UIWebViewDelegate {

    @IBOutlet weak var webView: UIWebView!
    var backBarButton: UIBarButtonItem!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        

        // Navigation Bar setup
        self.navigationItem.title = "Cobone Reviews"
        
        self.backBarButton = UIBarButtonItem.init(title: "Back", style: UIBarButtonItemStyle.Plain, target: self, action: #selector(self.back))
        let reloadBarButton = UIBarButtonItem.init(title: "Comment", style: UIBarButtonItemStyle.Plain, target: self, action: #selector(self.showComments))
        
        backBarButton.enabled = false;
        
        self.navigationItem.leftBarButtonItem = backBarButton
        self.navigationItem.rightBarButtonItem = reloadBarButton
        
        // Web View setup
        self.webView.delegate = self
        self.webView.loadRequest(NSURLRequest.init(URL: NSURL.init(string: "https://cobone.com")!))
        
        // SVProgressHUD setup
        SVProgressHUD.setDefaultStyle(SVProgressHUDStyle.Custom)
        SVProgressHUD.setDefaultAnimationType(SVProgressHUDAnimationType.Native)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // ----
    
    func back()  {
        self.webView.goBack();
    }
    
    func showComments()  {
        // TODO check the url, if a url for deal, then go to comment section, otherwise, show a message to the user
        self.webView.stringByEvaluatingJavaScriptFromString("window.location.hash='#comments-section'")
    }


    // -- UIWebViewDelegate
    
    func webViewDidStartLoad(webView: UIWebView) {
        SVProgressHUD.show()
        UIApplication.sharedApplication().networkActivityIndicatorVisible = true
    }
    
    func webViewDidFinishLoad(webView: UIWebView) {
        SVProgressHUD.dismiss()
        UIApplication.sharedApplication().networkActivityIndicatorVisible = false
        
        if (self.webView.canGoBack){
            self.backBarButton.enabled = true;
        }else{
            self.backBarButton.enabled = false;
        }
        
        if (!webView.loading){
            injectScript();
        }
    }
    
    // -- private methods
    
    private func injectScript() {
        debugPrint("try injecting scripts")
        
        let scripts = ["lib/jquery.min", "messages", "translate", "conf", "comments"].map {
            getFileContent("chrome-extension/" + $0, ofType: "js");
            }.joinWithSeparator(" ");

        self.webView.stringByEvaluatingJavaScriptFromString(scripts);
    }
    
    private func getFileContent(fileName: String, ofType: String) -> String {
        let path = NSBundle.mainBundle().pathForResource(fileName, ofType: ofType);
        
        var content: String?
        do {
            content = try String(contentsOfFile: path!, encoding: NSUTF8StringEncoding)
        } catch _ {
            content = nil
        }
        
        return content!;
    }
}

