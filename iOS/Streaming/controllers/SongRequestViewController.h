//
//  SongRequestViewControllerViewController.h
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "UnicaradioBaseViewController.h"

#import "LoadingDialog.h"

#import "US2ValidatorUIDelegate.h"
#import "US2ValidatorUIProtocol.h"

#import "TooltipView.h"
#import "FormTableViewCell.h"

#define kPlistname @"Information.plist"

#define EMAIL_POSITION 0
#define AUTHOR_POSITION 1
#define TITLE_POSITION 2

@interface SongRequestViewController : UnicaradioBaseViewController<US2ValidatorUIDelegate,
														FormTableViewCellDelegate,
														UITextFieldDelegate,
														UITextViewDelegate,
														UITableViewDelegate,
														UITableViewDataSource>
{
@private
	LoadingDialog *dialog;

	NSOperationQueue *queue;

	TooltipView    *_tooltipView;
    id <US2ValidatorUIProtocol> _tooltipConnectedTextUI;

	NSMutableArray *textFields;

	IBOutlet UITableView *tableView;
}

@property (strong, nonatomic) IBOutlet UITableView *tableView;

@end
