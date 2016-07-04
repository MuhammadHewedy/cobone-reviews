//
//  ViewController.swift
//  Cobon Reviews
//
//  Created by Muhammad Hewedy on 7/3/16.
//  Copyright © 2016 Muhammad Hewedy. All rights reserved.
//

import UIKit
import WebKit
import SVProgressHUD

class ViewController: UIViewController, WKNavigationDelegate {

    var webView: WKWebView!
    var backBarButton: UIBarButtonItem!
    var reloadBarButton: UIBarButtonItem!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        

        // Navigation Bar setup
        self.navigationItem.title = NSLocalizedString("app.title", comment: "app.title")
        
        self.backBarButton = UIBarButtonItem.init(title: NSLocalizedString("back", comment: "back"), style: UIBarButtonItemStyle.Plain, target: self, action: #selector(self.back))
        self.reloadBarButton = UIBarButtonItem.init(barButtonSystemItem: UIBarButtonSystemItem.Refresh, target: self, action: #selector(self.reload))

        backBarButton.enabled = false
        self.navigationItem.leftBarButtonItem = backBarButton
        self.navigationItem.rightBarButtonItem = reloadBarButton
        
        // Web View setup
        let wkConfig = WKWebViewConfiguration()
        self.webView = WKWebView.init(frame: self.view.frame, configuration: wkConfig)
        self.webView.allowsBackForwardNavigationGestures = true
        self.webView.navigationDelegate = self
        self.webView.loadRequest(NSURLRequest.init(URL: NSURL.init(string: "https://cobone.com")!))
        self.view.addSubview(self.webView)
        
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
    
    func reload()  {
        self.webView.reload()
    }


    // -- WKNavigationDelegate --------------------------
    
    func webView(webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        debugPrint("didStartProvisionalNavigation")
        
        SVProgressHUD.show()
        UIApplication.sharedApplication().networkActivityIndicatorVisible = true
    }
    
    func webView(webView: WKWebView, didCommitNavigation navigation: WKNavigation!) {
        debugPrint("didCommitNavigation")
    }
    
    func webView(webView: WKWebView, didFinishNavigation navigation: WKNavigation!) {
        debugPrint("didFinishNavigation")
        
        SVProgressHUD.dismiss()
        UIApplication.sharedApplication().networkActivityIndicatorVisible = false
        
        if (self.webView.canGoBack){
            self.backBarButton.enabled = true;
        }else{
            self.backBarButton.enabled = false;
        }
        
        injectScript();
    }
    
    func webView(webView: WKWebView, didFailNavigation navigation: WKNavigation!, withError error: NSError) {
        debugPrint("didFailNavigation")
        
        SVProgressHUD.dismiss()
        UIApplication.sharedApplication().networkActivityIndicatorVisible = false
    }
    
    func webView(webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: NSError) {
        debugPrint("didFailProvisionalNavigation")
        
        SVProgressHUD.dismiss()
        UIApplication.sharedApplication().networkActivityIndicatorVisible = false
    }
    
    
    // -- private methods
    
    private func injectScript() {
        debugPrint("try injecting scripts")
        
        let scripts = ["lib/jquery.min", "messages", "translate", "conf", "comments"].map {
            loadScript($0);
            }.joinWithSeparator(" ");
        
        self.webView.evaluateJavaScript(scripts, completionHandler: nil)
    }
    
    private func loadScript(fileName: String) -> String {
        let path = NSBundle.mainBundle().pathForResource("chrome-extension/" + fileName, ofType: "js");
        
        var content: String?
        do {
            content = try String(contentsOfFile: path!, encoding: NSUTF8StringEncoding)
        } catch _ {
            content = nil
        }
        
        return content!;
    }
}

