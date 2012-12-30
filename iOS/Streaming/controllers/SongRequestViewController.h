//
//  SongRequestViewControllerViewController.h
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../widgets/LoadingDialog.h"

#define kPlistname @"Information.plist"

@interface SongRequestViewController : UIViewController<UITextFieldDelegate>
{
	IBOutlet UIScrollView *scrollView;
    IBOutlet UIView *contentView;

	IBOutlet UITextField *emailTextView;
	IBOutlet UITextField *autoreTextView;
	IBOutlet UITextField *titoloTextView;

	LoadingDialog *dialog;

	NSOperationQueue *queue;
	NSTimer *timer;

	CGFloat animatedDistance;
}

@property (nonatomic, strong) IBOutlet UIScrollView *scrollView;
@property (nonatomic, strong) IBOutlet UIView *contentView;

@property (nonatomic, strong) IBOutlet UITextField *emailTextView;
@property (nonatomic, strong) IBOutlet UITextField *autoreTextView;
@property (nonatomic, strong) IBOutlet UITextField *titoloTextView;

- (IBAction) sendEmail:(id)sender;

@end
